<#include "module/macro.ftl">
<@layout title="标签云 | ${options.blog_title?default('Material')}" keywords="${options.seo_keywords?default('Material')}" description="${options.seo_desc?default('Material')}">
    <#include "_widget/page-tagcloud.ftl">
</@layout>