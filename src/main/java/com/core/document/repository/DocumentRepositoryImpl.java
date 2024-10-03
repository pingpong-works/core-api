package com.core.document.repository;

import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.document.entity.QDocument;
import com.core.type.entity.QDocumentType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DocumentRepositoryImpl implements DocumentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public DocumentRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Document> findDocument(DocumentDto.DocumentSearch search, Pageable pageable) {

        QDocument document = QDocument.document;
        QDocumentType docsType = QDocumentType.documentType;

        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 필터 , 검색 날짜 사이에 생성된 문서 조회
        if (search.getSearchStartDate() != null && search.getSearchEndDate() != null) {
            LocalDateTime searchStartDate = search.getSearchStartDate().atStartOfDay();
            LocalDateTime searchEndDate = search.getSearchEndDate().atTime(23, 59, 59);
            builder.and(document.createdAt.between(searchStartDate, searchEndDate));
        }

        //부서명 필터
        if(search.getDepartmentName() != null && !search.getDepartmentName().trim().isEmpty()) {
            builder.and(document.departmentName.containsIgnoreCase(search.getDepartmentName())) ;
        }

        //작성자 필터
        if(search.getAuthor() != null && !search.getAuthor().trim().isEmpty()) {
            builder.and(document.author.containsIgnoreCase(search.getAuthor())) ;
        }

        //document 상태 필터
        if(search.getDocumentStatus() != null) {
            builder.and(document.documentStatus.eq(search.getDocumentStatus())) ;
        }

        //document 제목 필터
        if(search.getTitle() != null && !search.getTitle().trim().isEmpty())  {
            builder.and(document.title.containsIgnoreCase(search.getTitle())) ;
        }

        //docsType 필터
        if(search.getType() != null && !search.getType().trim().isEmpty())  {
            builder.and(document.documentType.type.containsIgnoreCase(search.getType())) ;
        }

        List<OrderSpecifier<?>> orderSpecifiers = getSortOrder(pageable, document);

        List<Document> results = queryFactory
                .selectFrom(document)
                .leftJoin(document.documentType, docsType)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(document)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);

    }

    private List<OrderSpecifier<?>> getSortOrder(Pageable pageable, QDocument document) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(document.getType(), document.getMetadata());
            orders.add(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
        }
        return orders;
    }
}
