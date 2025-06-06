package cn.easyes.starter.register;

import cn.easyes.common.utils.EEVersionUtils;
import cn.easyes.common.utils.LogUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.easyes.common.constants.BaseEsConstants.ENABLE_BANNER;
import static cn.easyes.common.constants.BaseEsConstants.ENABLE_PREFIX;

/**
 * 注册bean
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public class MapperScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Boolean enable = Optional.ofNullable(environment.getProperty(ENABLE_PREFIX)).map(Boolean::parseBoolean).orElse(Boolean.TRUE);
        if (!enable) {
            LogUtils.info("===> Easy-Es is not enabled");
            return;
        }

        //打印banner @author dazer007
        boolean banner = Optional.ofNullable(environment.getProperty(ENABLE_BANNER)).map(Boolean::parseBoolean).orElse(Boolean.TRUE);
        if (banner) {
            String versionStr = EEVersionUtils.getJarVersion(this.getClass());
            System.out.println("\n" +
                    "___                     _  _            ___\n" +
                    "  | __|   __ _     ___    | || |   ___    | __|    ___\n" +
                    "  | _|   / _` |   (_-<     \\_, |  |___|   | _|    (_-<\n" +
                    "  |___|  \\__,_|   /__/_   _|__/   _____   |___|   /__/_\n" +
                    "_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_| \"\"\"\"|_|     |_|\"\"\"\"\"|_|\"\"\"\"\"|\n" +
                    "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\n" +
                    "------------------------------------------------------>"
            );

            // 版本长度并不固定,比如beta版,所以需要特殊处理
            int width = 38;
            int blank = width - versionStr.length();
            StringBuilder sb = new StringBuilder();
            sb.append(":: version   :: ")
                    .append(versionStr);
            for (int i = 0; i < blank; i++) {
                sb.append(" ");
            }
            sb.append(">");
            System.out.println(sb);
            System.out.println(":: home      :: https://easy-es.cn/                   >");
            System.out.println(":: community :: https://dromara.org/                  >");
            System.out.println(":: wechat    :: 252645816, add and become muscle man! >");
            System.out.println("------------------------------------------------------>");
        }

        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(EsMapperScan.class.getName()));
        if (mapperScanAttrs != null) {
            registerBeanDefinitions(mapperScanAttrs, registry);
        }
    }

    void registerBeanDefinitions(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        // this check is needed in Spring 3.1
        Optional.ofNullable(resourceLoader).ifPresent(scanner::setResourceLoader);
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(
                Arrays.stream(annoAttrs.getStringArray("value"))
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList()));

        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        System.out.println("252645816, add and become muscle man! >".length());
    }
}
