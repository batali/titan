package com.thinkaurelius.titan.diskstorage.es;

import com.thinkaurelius.titan.StorageSetup;
import com.thinkaurelius.titan.core.Parameter;
import com.thinkaurelius.titan.core.attribute.*;
import com.thinkaurelius.titan.diskstorage.StorageException;
import com.thinkaurelius.titan.diskstorage.indexing.IndexProvider;
import com.thinkaurelius.titan.diskstorage.indexing.IndexProviderTest;
import com.thinkaurelius.titan.core.Mapping;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import static com.thinkaurelius.titan.diskstorage.es.ElasticSearchIndex.CLIENT_ONLY_KEY;
import static com.thinkaurelius.titan.diskstorage.es.ElasticSearchIndex.LOCAL_MODE_KEY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class ElasticSearchIndexTest extends IndexProviderTest {

    @Override
    public IndexProvider openIndex() throws StorageException {
        return new ElasticSearchIndex(getLocalESTestConfig());
    }

    @Override
    public boolean supportsLuceneStyleQueries() {
        return true;
    }

    public static final Configuration getLocalESTestConfig() {
        Configuration config = new BaseConfiguration();
        config.setProperty(LOCAL_MODE_KEY, true);
        config.setProperty(CLIENT_ONLY_KEY, false);
        config.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, StorageSetup.getHomeDir("es"));
        return config;
    }


    @Test
    public void testSupport() {
        assertTrue(index.supports(of(String.class)));
        assertTrue(index.supports(of(String.class, new Parameter("mapping", Mapping.TEXT))));
        assertTrue(index.supports(of(String.class, new Parameter("mapping",Mapping.STRING))));

        assertTrue(index.supports(of(Double.class)));
        assertFalse(index.supports(of(Double.class, new Parameter("mapping",Mapping.TEXT))));

        assertTrue(index.supports(of(Long.class)));
        assertTrue(index.supports(of(Long.class, new Parameter("mapping",Mapping.DEFAULT))));
        assertTrue(index.supports(of(Integer.class)));
        assertTrue(index.supports(of(Short.class)));
        assertTrue(index.supports(of(Byte.class)));
        assertTrue(index.supports(of(Float.class)));
        assertTrue(index.supports(of(Geoshape.class)));
        assertFalse(index.supports(of(Object.class)));
        assertFalse(index.supports(of(Exception.class)));

        assertTrue(index.supports(of(String.class), Text.CONTAINS));
        assertTrue(index.supports(of(String.class, new Parameter("mapping", Mapping.TEXT)), Text.PREFIX));
        assertTrue(index.supports(of(String.class), Text.REGEX));
        assertFalse(index.supports(of(String.class, new Parameter("mapping",Mapping.STRING)), Text.CONTAINS));
        assertTrue(index.supports(of(String.class, new Parameter("mapping",Mapping.STRING)), Cmp.EQUAL));
        assertTrue(index.supports(of(String.class, new Parameter("mapping",Mapping.STRING)), Cmp.NOT_EQUAL));

        assertTrue(index.supports(of(Double.class), Cmp.EQUAL));
        assertTrue(index.supports(of(Double.class), Cmp.GREATER_THAN_EQUAL));
        assertTrue(index.supports(of(Double.class), Cmp.LESS_THAN));
        assertTrue(index.supports(of(Double.class, new Parameter("mapping",Mapping.DEFAULT)), Cmp.LESS_THAN));
        assertFalse(index.supports(of(Double.class, new Parameter("mapping",Mapping.TEXT)), Cmp.LESS_THAN));
        assertTrue(index.supports(of(Geoshape.class), Geo.WITHIN));

        assertFalse(index.supports(of(Double.class), Geo.INTERSECT));
        assertFalse(index.supports(of(Long.class), Text.CONTAINS));
        assertFalse(index.supports(of(Geoshape.class), Geo.DISJOINT));
    }

}
