/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not
 * responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL), Version 2.1 which is
 * available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.feature;

import org.broad.igv.feature.tribble.GFFCodec;
import org.broad.igv.track.GFFFeatureSource;
import org.broad.igv.track.TribbleFeatureSource;
import org.broad.igv.util.ResourceLocator;
import org.broad.igv.util.TestUtils;
import htsjdk.tribble.Feature;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.*;


/**
 * @author Jim Robinson
 * @date 5/10/12
 */
public class GFFTest{//} extends AbstractHeadlessTest{


    private List<Feature> getFeatures(String filePath) throws Exception{

        GFFFeatureSource src =  new GFFFeatureSource(TribbleFeatureSource.getFeatureSource(new ResourceLocator(filePath), null));

        List<Feature> features = new ArrayList<Feature>();
        Iterator<Feature> iter = src.getFeatures("chr1", 0, Integer.MAX_VALUE);
        while(iter.hasNext()) features.add(iter.next());

        return features;
    }
    /**
     * This test verifies that the attributes from column 9 are retained for a CDS feature that does not have a parent.
     * This bugfix was released with 2.1.12.
     *
     * @throws Exception
     */
    @Test
    public void testLoadSingleCDS() throws Exception {

        String gtfFile = TestUtils.DATA_DIR + "gtf/single_cds.gtf";
        List<Feature> features = getFeatures(gtfFile);

        assertEquals(1, features.size());
        BasicFeature bf = (BasicFeature) features.get(0);

        Exon exon = bf.getExons().get(0);
        assertNotNull(exon.getAttributes());
    }

    /**
     * Test that we parse the various spellings of "color" correctly
     * v2.2.0/2.2.1 had a bug where an attribute would be checked for
     * but a different one accessed
     * @throws Exception
     */
    @Test
    public void testGFFColorSpellings() throws Exception{
        String gffFile = TestUtils.DATA_DIR + "gff/color_sps.gff";
        List<Feature> features = getFeatures(gffFile);

        assertEquals("Error parsing certain features", 6, features.size());

        for(Feature f: features){
            BasicFeature bf = (BasicFeature) f;
            boolean haveColor = bf.getColor() != null;
            if(bf.hasExons()){
                for(Exon ex: bf.getExons()){
                    haveColor |= ex.getColor() != null;
                }
            }
            assertTrue(haveColor);
        }
    }

    @Test
    public void testGFF2_parentIds() throws Exception{
        String path = TestUtils.DATA_DIR + "gff/parentIds.gff";
        List<Feature> features = getFeatures(path);

        assertEquals(1, features.size());
        BasicFeature bf = (BasicFeature) features.get(0);

        int numExons = 17;
        assertEquals(numExons, bf.getExons().size());
        Exon lastExon = bf.getExons().get(numExons - 1);

        assertTrue(lastExon.isNonCoding());
        assertEquals(3807030 - 1, lastExon.getStart());
    }
}
