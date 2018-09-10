/**
 * Copyright (c) 2015-2018, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.session;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

/**
 * 实现基于 J2Cache 的分布式的 Session 管理
 * @author Winter Lau (javayou@gmail.com)
 */
public class J2CacheSessionFilter implements Filter {

    private CacheFacade cache;

    private String region;
    private String cookieName;
    private String cookiePath;
    private String cookieDomain;
    private int cookieMaxAge;

    @Override
    public void init(FilterConfig config) {
        this.region         = config.getInitParameter("j2cache.region");
        this.cookieName     = config.getInitParameter("cookie.name");
        this.cookieDomain   = config.getInitParameter("cookie.domain");
        this.cookiePath     = config.getInitParameter("cookie.path");
        this.cookieMaxAge = Integer.parseInt(config.getInitParameter("cookie.maxAge"));

        this.cache = new CacheFacade(this.region, 2000, 30, null);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest j2cacheRequest = new J2CacheRequestWrapper(req, res);
        chain.doFilter(j2cacheRequest, res);
    }

    @Override
    public void destroy() {
        cache.close();
    }


    /*************************************************
     *
     * request 封装，用于重新处理 session 的实现
     *
     *************************************************/
    private class J2CacheRequestWrapper extends HttpServletRequestWrapper {

        private HttpServletResponse response;

        public J2CacheRequestWrapper(ServletRequest req, ServletResponse res) {
            super((HttpServletRequest)req);
            this.response = (HttpServletResponse)res;
        }

        @Override
        public HttpSession getSession(boolean create) {
            Cookie ssnCookie = getCookie(cookieName);
            J2CacheSession session = null;
            if (ssnCookie != null) {
                String session_id = ssnCookie.getValue();
                //read session from j2cache
                session.setNew(false);
            }
            else if(create) {
                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                //save session to j2cache
                //write session to cookie
            }
            return session;
        }

        @Override
        public HttpSession getSession() {
            return this.getSession(true);
        }
        /**
         * Get cookie object by cookie name.
         */
        private Cookie getCookie(String name) {
            Cookie[] cookies = ((HttpServletRequest) getRequest()).getCookies();
            if (cookies != null)
                for (Cookie cookie : cookies)
                    if (cookie.getName().equalsIgnoreCase(name))
                        return cookie;
            return null;
        }

        /**
         * @param name
         * @param value
         * @param maxAgeInSeconds
         */
        private void setCookie(String name, String value, int maxAgeInSeconds) {
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(maxAgeInSeconds);
            cookie.setPath(cookiePath);
            if (cookieDomain != null) {
                cookie.setDomain(cookieDomain);
            }
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

    }

}
