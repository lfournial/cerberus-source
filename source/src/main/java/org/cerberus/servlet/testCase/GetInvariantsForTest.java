/*
 * Cerberus  Copyright (C) 2013  vertigo17
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.servlet.testCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.cerberus.entity.Invariant;
import org.cerberus.log.MyLogger;
import org.cerberus.service.IInvariantService;
import org.cerberus.service.impl.InvariantService;
import org.cerberus.servlet.invariant.GetInvariantList;
import org.cerberus.util.SqlUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * {Insert class description here}
 *
 * @author Frederic LESUR
 * @version 1.0, 19/03/2013
 * @since 2.0.0
 */
//@WebServlet(value = "/GetInvariantsForTest")
public class GetInvariantsForTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        IInvariantService invariantService = appContext.getBean(InvariantService.class);
        try {
            List<String> values = new ArrayList<String>();
            values.add("COUNTRY");
            values.add("RUNQA");
            values.add("RUNUAT");
            values.add("RUNPROD");
            values.add("PRIORITY");
            values.add("GROUP");
            values.add("TCSTATUS");
            values.add("TCACTIVE");
            values.add("BUILD");
            values.add("REVISION");

            JSONObject jsonResponse = new JSONObject();

            HashMap<String,List<String>> invariants = new HashMap<String,List<String>>();
            
            for (Invariant myInvariant : invariantService.findInvariantPrivateListByCriteria(0, 0, "sort", "ASC", "%", "idname "+SqlUtil.getInSQLClause(values))) {
                if(invariants.containsKey(myInvariant.getIdName())) {
                    invariants.get(myInvariant.getIdName()).add(myInvariant.getValue());
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(myInvariant.getValue());
                    invariants.put(myInvariant.getIdName(),list);
                }
            }

            for (Invariant myInvariant : invariantService.findInvariantPublicListByCriteria(0, 0, "sort", "ASC", "%", "idname "+SqlUtil.getInSQLClause(values))) {
                if(invariants.containsKey(myInvariant.getIdName())) {
                    invariants.get(myInvariant.getIdName()).add(myInvariant.getValue());
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(myInvariant.getValue());
                    invariants.put(myInvariant.getIdName(),list);
                }
            }

            for(Map.Entry<String,List<String>> key: invariants.entrySet()) {
                JSONArray jSONArray = new JSONArray(key.getValue());

                jsonResponse.put(key.getKey(),jSONArray);
            }

            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().print(jsonResponse.toString());
        } catch (JSONException e) {
            MyLogger.log(GetInvariantList.class.getName(), Level.FATAL, "" + e);
            httpServletResponse.setContentType("text/html");
            httpServletResponse.getWriter().print(e.getMessage());
        }
    }
}