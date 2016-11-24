package org.gruth.utils.zoo;

import groovy.lang.Binding;
import org.gruth.utils.LinkedTaggedsMap;
import org.gruth.utils.Loads;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author bamade
 */
// Date: 27/04/15

public class ZooUtils {
    static HashMap<Class, LinkedTaggedsMap> zooMap =
            new HashMap<>() ;
    public static <T> LinkedTaggedsMap<T> getZoo(Class<T> clazz) {
        LinkedTaggedsMap mappedMap =
                zooMap.get(clazz) ;
        if(mappedMap == null) {
            Object res = null ;
            try {
                res = Loads.loadEval(ZooUtils.class, new Binding(), clazz);
                if(res == null) {
                    return null ;
                }
            } catch (Exception exc) {
                //todo use Logger
                return null ;
            }
            mappedMap = (LinkedTaggedsMap<T>) res;
            zooMap.put(clazz, mappedMap) ;
        }
            return mappedMap ;
    }

    public static <T> Iterable<T> getValuesFor(Class<T> clazz) {
        LinkedTaggedsMap<T> linkedMap = getZoo(clazz) ;
        if(linkedMap == null) {
            return Collections.EMPTY_LIST ;
        }
        return linkedMap.getValuesIterable();

    }
    public static <T> Iterable<T> getValuesFor(Class<T> clazz, String name) {
        LinkedTaggedsMap<T> linkedMap = getZoo(clazz) ;
        if(linkedMap == null) {
            return Collections.EMPTY_LIST ;
        }
        String[] path = name.split("\\.");
        return linkedMap.getValuesIterable(path);
    }
}
