/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
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
package net.oschina.j2cache.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nustaq.kson.Kson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * TODO 使用 fastjson 实现序列化
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class JSONSerializer implements Serializer {

    private static final List<Class> primitiveClasses = new ArrayList(){{
        add(Number.class);
        add(Character.class);
        add(CharSequence.class);
    }};

    @Override
    public String name() {
        return "json";
    }

    @Override
    public byte[] serialize(Object obj) {
        if(!primitiveClasses.contains(obj.getClass())){
            HashMap<String, Object> val = new HashMap<>();
            val.put("value", obj);
            val.put("__class__", obj.getClass().getName());
            String jsonStr = JSON.toJSONString(obj);
            return jsonStr.getBytes();
        }
        String jsonStr = JSON.toJSONString(obj);
        return jsonStr.getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) {
        Object obj = JSON.parse(new String(bytes));
        if(obj instanceof JSONObject){

        }
        return obj;
    }

    public static void main(String[] args) throws Exception {
        JSONSerializer json = new JSONSerializer();
        int[] i = {10,11,12};
        //Date i = new Date(2018,10,1);//"100";
        String result = new String(json.serialize(i));
        new Kson().readObject(result);
        System.out.println(json.deserialize(result.getBytes()));
        System.out.println(JSON.parse(result).getClass().getName());


        System.out.println(new Kson().writeObject(i));
    }
}
