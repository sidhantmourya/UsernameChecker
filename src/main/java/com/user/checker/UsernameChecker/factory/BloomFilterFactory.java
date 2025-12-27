package com.user.checker.UsernameChecker.factory;

import com.user.checker.UsernameChecker.filter.interfaces.BloomFilterIF;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class BloomFilterFactory {

//    private final Map<String, BloomFilterIF> filters = new ConcurrentHashMap<>();
    private final ListableBeanFactory beanFactory;

    public BloomFilterFactory(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
//        init();
    }
/*
    private void init() {
        Map<String, BloomFilterIF> beans =  beanFactory.getBeansOfType(BloomFilterIF.class);
        filters.putAll(beans);
    }*/

    public BloomFilterIF getFilter(String strategy)
    {
        return beanFactory.getBean(strategy, BloomFilterIF.class);
    }

    public Set<String> getAvailableStrategies()
    {
        return beanFactory.getBeansOfType(BloomFilterIF.class).keySet();
    }

    public boolean isStrategyAvailable(String strategy)
        {
        return beanFactory.containsBean(strategy);
    }
}
