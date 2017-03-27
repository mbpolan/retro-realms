package com.mbpolan.retrorealms.services.support;

import com.mbpolan.retrorealms.tmx.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * @author mbpolan
 */
public class TmxMapLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TmxMapLoader.class);

    public void load(InputStream in) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Map.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();

            Map mapType = (Map) unmarshaller.unmarshal(in);
            LOG.debug("Successfully read TMX map data");
        }

        catch (JAXBException ex) {
            LOG.error("Failed to load TMX map", ex);
        }
    }
}
