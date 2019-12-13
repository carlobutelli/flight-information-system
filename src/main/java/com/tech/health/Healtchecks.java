package com.tech.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class Healtchecks implements HealthIndicator {
    @Autowired
    JdbcTemplate template;
    @Override
    public Health health() {
        if (check() != 1) {
            return Health.down().withDetail("Error Code", 500).build();
        }
        return Health.up().build();
    }

    private int check(){
        List<Object> results = template.query("select 1;", new SingleColumnRowMapper<>());
        return results.size();
    }
}