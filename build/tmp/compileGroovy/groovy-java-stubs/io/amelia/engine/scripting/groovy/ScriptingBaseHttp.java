package io.amelia.engine.scripting.groovy;

import com.chiorichan.utils.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.transform.CompileStatic() public abstract class ScriptingBaseHttp
  extends com.chiorichan.factory.api.Builtin  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  com.chiorichan.net.http.HttpRequestWrapper getRequest() { return (com.chiorichan.net.http.HttpRequestWrapper)null;}
public  com.chiorichan.net.http.HttpResponseWrapper getResponse() { return (com.chiorichan.net.http.HttpResponseWrapper)null;}
public  void header(java.lang.String header) { }
public  void header(java.lang.String key, java.lang.String val) { }
public  com.chiorichan.session.Session getSession() { return (com.chiorichan.session.Session)null;}
public  com.chiorichan.permission.PermissibleEntity getPermissibleEntity() { return (com.chiorichan.permission.PermissibleEntity)null;}
public  com.chiorichan.account.Account getAccount(java.lang.String uid) { return (com.chiorichan.account.Account)null;}
public  java.util.List<com.chiorichan.account.AccountMeta> getAccounts(java.lang.String query) { return (java.util.List<com.chiorichan.account.AccountMeta>)null;}
public  java.util.List<com.chiorichan.account.AccountMeta> getAccounts(java.lang.String query, int limit) { return (java.util.List<com.chiorichan.account.AccountMeta>)null;}
public  com.chiorichan.account.Account getAccount() { return (com.chiorichan.account.Account)null;}
public  com.chiorichan.account.Account getAccountOrNull() { return (com.chiorichan.account.Account)null;}
public  com.chiorichan.account.Account getAccountOrFail() { return (com.chiorichan.account.Account)null;}
public  boolean hasLogin() { return false;}
@java.lang.Override() public  com.chiorichan.site.Site getSite() { return (com.chiorichan.site.Site)null;}
public  void requireLogin() { }
public  java.lang.String url_to_login() { return (java.lang.String)null;}
public  java.lang.String url_to_logout() { return (java.lang.String)null;}
public  void define(java.lang.String key, java.lang.Object val) { }
public  java.io.File dirname() { return (java.io.File)null;}
public  java.lang.String domain() { return (java.lang.String)null;}
public  java.lang.String domain(java.lang.String subdomain) { return (java.lang.String)null;}
public  io.amelia.storage.old.DatabaseEngineLegacy getDatabase() { return (io.amelia.storage.old.DatabaseEngineLegacy)null;}
public  io.amelia.logging.Logger getLogger() { return (io.amelia.logging.Logger)null;}
public  com.chiorichan.net.http.Nonce nonce() { return (com.chiorichan.net.http.Nonce)null;}
public  java.lang.String base_url() { return (java.lang.String)null;}
public  java.lang.String route_id(java.lang.String id)throws io.amelia.lang.SiteConfigurationException { return (java.lang.String)null;}
public  java.lang.String route_id(java.lang.String id, java.util.List<java.lang.String> params)throws io.amelia.lang.SiteConfigurationException { return (java.lang.String)null;}
public  java.lang.String route_id(java.lang.String id, java.util.Map<java.lang.String, java.lang.String> params)throws io.amelia.lang.SiteConfigurationException { return (java.lang.String)null;}
public  java.lang.String url_id(java.lang.String id)throws io.amelia.lang.SiteConfigurationException { return (java.lang.String)null;}
public  java.lang.String url_id(java.lang.String id, boolean ssl)throws io.amelia.lang.SiteConfigurationException { return (java.lang.String)null;}
public  java.lang.String url_id(java.lang.String id, java.lang.String prefix)throws io.amelia.lang.SiteConfigurationException { return (java.lang.String)null;}
public  java.lang.String url_to() { return (java.lang.String)null;}
public  java.lang.String url_to(java.lang.String subdomain) { return (java.lang.String)null;}
public  java.lang.String url_to(java.lang.String subdomain, boolean secure) { return (java.lang.String)null;}
public  java.lang.String get_map() { return (java.lang.String)null;}
public  java.lang.String get_append(java.util.Map<java.lang.String, java.lang.Object> map) { return (java.lang.String)null;}
public  java.lang.String get_append(java.lang.String key, java.lang.Object val) { return (java.lang.String)null;}
public  java.lang.String uri_to() { return (java.lang.String)null;}
public  java.lang.String uri_to(boolean secure) { return (java.lang.String)null;}
public  java.lang.String uri_to(java.lang.String subdomain) { return (java.lang.String)null;}
public  java.lang.String uri_to(java.lang.String subdomain, boolean ssl) { return (java.lang.String)null;}
public  java.lang.String uri_to(java.lang.String subdomain, java.lang.String prefix) { return (java.lang.String)null;}
public  com.chiorichan.factory.ScriptingFactory getScriptingFactory() { return (com.chiorichan.factory.ScriptingFactory)null;}
public  boolean isAdmin() { return false;}
public  boolean isOp() { return false;}
public  com.chiorichan.permission.PermissionResult checkPermission(java.lang.String perm) { return (com.chiorichan.permission.PermissionResult)null;}
public  com.chiorichan.permission.PermissionResult checkPermission(com.chiorichan.permission.Permission perm) { return (com.chiorichan.permission.PermissionResult)null;}
public  com.chiorichan.permission.PermissionResult requirePermission(java.lang.String perm) { return (com.chiorichan.permission.PermissionResult)null;}
public  com.chiorichan.permission.PermissionResult requirePermission(com.chiorichan.permission.Permission perm) { return (com.chiorichan.permission.PermissionResult)null;}
public  com.chiorichan.factory.ScriptingFactory getEvalFactory() { return (com.chiorichan.factory.ScriptingFactory)null;}
public  java.lang.Object include(java.lang.String pack)throws io.amelia.lang.MultipleException, io.amelia.lang.ScriptingException { return null;}
public  java.lang.Object require(java.lang.String pack)throws java.io.IOException, io.amelia.lang.MultipleException, io.amelia.lang.ScriptingException { return null;}
public  com.chiorichan.factory.models.SQLModelBuilder model(java.lang.String pack)throws java.io.IOException, io.amelia.lang.MultipleException, io.amelia.lang.ScriptingException { return (com.chiorichan.factory.models.SQLModelBuilder)null;}
}
