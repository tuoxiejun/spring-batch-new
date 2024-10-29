package com.example.chatprer07_12.batch;

import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CustomerByCityQueryProvider extends AbstractJpaQueryProvider {

    private String city;

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Query createQuery() {
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("select c from Customer c where c.city = :city");
        query.setParameter("city", city);
        return query;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(city, "The city must not be null!");
    }
}
