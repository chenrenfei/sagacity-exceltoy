<?xml version="1.0" encoding="UTF-8"?>
<tables>
<#if (orderTables?exists && orderTables?size>0)>
<#list orderTables as table>
	<!-- <#if (table.tableRemark?exists && table.tableRemark!='')>${table.tableRemark}</#if> -->
	<#if (table.unMatchTable?exists && table.unMatchTable=='false')>
	<table name="${table.tableName}" dist="${table.tableName}" fkFilter="false" active="true"/>
	<#else>
	<table name="${table.tableName}" dist="${table.tableName}" fkFilter="false" active="true">
	<![CDATA[
		<#if (table.colMetas?exists)>
		<#assign paramCnt="0"/>
		select <#list table.colMetas as columnMeta><#if (paramCnt=='1')>,</#if><#assign paramCnt='1'/>${columnMeta.colName}</#list>
		from ${table.tableName}
		</#if>
	]]>
	</table>
	</#if>
</#list>
</#if>	
</tables>