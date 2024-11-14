package com.autohome.car.api.common;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;

import java.io.Serializable;

public class EhCacheCopyStrategy implements ReadWriteCopyStrategy<Element> {
    @Override
    public Element copyForWrite(Element element, ClassLoader classLoader) {
        if (element == null)
            return element;
        Object temp = (Serializable) element.getObjectValue();
        return new Element(element.getObjectKey(), temp);
    }

    @Override
    public Element copyForRead(Element element, ClassLoader classLoader) {
        if(element==null)
            return element;
        Object temp = (Serializable)element.getObjectValue();
       // return new Element(element.getObjectKey(),temp);
        return new Element(element.getObjectKey(), temp, element.getVersion(), element.getCreationTime(), element.getLastAccessTime(), element.getLastUpdateTime(), element.getHitCount());
    }
}
