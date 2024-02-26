package com.ej.hgj.config.dao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.ej.hgj.sy.dao", sqlSessionFactoryRef = "SySqlSessionFactory")
public class SyDataSourceConfig {

    @Bean(name = "SyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sy")
    public DataSource getSecondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "SySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("SyDataSource") DataSource datasource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(datasource);
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:com/ej/hgj/sy/dao/**/*Mapper.xml"));
        return bean.getObject();// 设置mybatis的xml所在位置
    }

    @Bean("SySqlSessionTemplate")
    public SqlSessionTemplate secondarySqlSessionTemplate(
            @Qualifier("SySqlSessionFactory") SqlSessionFactory sessionfactory) {
        return new SqlSessionTemplate(sessionfactory);
    }
}
