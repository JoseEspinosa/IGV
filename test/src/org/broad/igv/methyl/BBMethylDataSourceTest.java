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

package org.broad.igv.methyl;

import junit.framework.Assert;
import org.broad.igv.bbfile.BBFileReader;
import org.broad.igv.feature.Strand;
import org.broad.igv.feature.genome.Genome;
import org.broad.igv.util.TestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test of data source for Michael Ziller's hacked up bigbed format for methylation scores.
 *
 * @author Jim Robinson
 * @date 4/19/12
 */
public class BBMethylDataSourceTest {


    @Test
    public void testUscQuery() throws Exception {

        // Test data
        // chr11	10001654	10001656	.	1000	.	100.000	7
        // chr11	10001667	10001669	.	833	.	83.333	6
        // chr11	10001752	10001754	.	625	.	62.500	8

        int[] expectedStarts = {10001654, 10001667, 10001752};
        int[] expectedEnds = {10001656, 10001669, 10001754};
        float[] expectedPercents = {100.0f, 83.333f, 62.500f};
        int[] expectedCounts = {7, 6, 8};

        String testFile = TestUtils.DATA_DIR +
                "/methylation/usc/jhu-usc.edu_UCEC.IlluminaHiSeq_WGBS.Level_3.1.0.0.hg18.SAMPLE.bb";

        Genome genome = null;  // Don't need a genome for this test

        String chr = "chr11";
        int start = 10001654;
        int end = 10001754;

        BBFileReader reader = new BBFileReader(testFile);
        MethylDataSource source = new BBMethylDataSource(reader, BBMethylDataSource.Type.USC, genome);
        Iterator<MethylScore> iter = source.query(chr, start, end);

        int i = 0;
        while (iter.hasNext()) {
            MethylScore ms = iter.next();
            assertEquals(chr, ms.getChr());
            assertEquals(expectedStarts[i], ms.getStart());
            assertEquals(expectedEnds[i], ms.getEnd());
            assertEquals(expectedPercents[i], ms.getScore(), .000001);
            assertEquals(expectedCounts[i], ms.getCount());
            assertEquals(Strand.NONE, ms.getStrand());
            i++;
        }
        assertEquals(3, i);


    }

    //@Test
    public void testZillerQuery() throws Exception {

        String testFile = "path-to-test-file.bb";
        String chr = "chr7";
        int start = 26133475;
        int end = 27333475;
        Genome genome = null;

        BBFileReader reader = new BBFileReader(testFile);
        MethylDataSource source = new BBMethylDataSource(reader, BBMethylDataSource.Type.ZILLER, genome);
        Iterator<MethylScore> iter = source.query(chr, start, end);

        // Should find at least 1 score.
        assertTrue(iter.hasNext());

        while (iter.hasNext()) {
            MethylScore score = iter.next();
            assertTrue(score.getEnd() >= start && score.getStart() <= end);
            assertTrue(score.getScore() >= 0 && score.getScore() <= 100);
        }
    }

    // @Test
    public void testCacheQuery() throws Exception {

        String testFile = "path-to-test-file.bb";
        String chr = "chr7";
        int start = 26133475;
        int end = 27333475;
        Genome genome = null;

        BBFileReader reader = new BBFileReader(testFile);
        MethylDataSource source = new BBMethylDataSource(reader, BBMethylDataSource.Type.ZILLER, genome);
        Iterator<MethylScore> iter = source.query(chr, start, end);
        List<MethylScore> nonCachedScores = new ArrayList<MethylScore>();
        while (iter.hasNext()) {
            nonCachedScores.add(iter.next());
        }

        int maxTileCount = 100000;
        int tileSize = 100;
        MethylDataSource cachedSource = new CachingMethylSource(new BBMethylDataSource(reader, BBMethylDataSource.Type.ZILLER, genome),
                maxTileCount, tileSize);
        Iterator<MethylScore> iter2 = cachedSource.query(chr, start, end);
        List<MethylScore> cachedScores = new ArrayList<MethylScore>();
        while (iter2.hasNext()) {
            cachedScores.add(iter2.next());
        }

        assertTrue(cachedScores.size() > 0);
        Assert.assertEquals(cachedScores.size(), nonCachedScores.size());
        for (int i = 0; i < cachedScores.size(); i++) {
            MethylScore cScore = cachedScores.get(i);
            MethylScore score = nonCachedScores.get(i);
            Assert.assertEquals(cScore.getStart(), score.getStart());
            Assert.assertEquals(cScore.getChr(), score.getChr());
        }
    }
}
