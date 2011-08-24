/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.component.bean.config.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanSwitchYardScannerDecodeURLBugTest {
    


    @Before
    public void setup(){
        //Let's avoid java.io.tmpdir to have a + sign
        System.setProperty("java.io.tmpdir", System.getProperty("java.io.tmpdir").replace("+", ""));
    }
    
    @Test
    public void test()  throws IOException {
    	scanDir("dir_without_plus_sign");
    	scanDir("dir_with_+++_sign");
    }
    
    private void scanDir(String tempDirName) throws IOException {
    
    	List<URL> urls = new ArrayList<URL>();

        //Let's create a temp dir with no + sign
        File tempDir = File.createTempFile(tempDirName, "myTestApp");
        tempDir.delete();
        tempDir.mkdir();

        //Let's copy a working sample switchyard app into the temp dir  
        FileUtils.copyDirectory(new File("./target/test-classes"), tempDir);
        urls.add(tempDir.toURI().toURL());
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
        
        //Let's scan the input dir
        assertDirectoryWasProperlyScanned(input);
        tempDir.delete();
    }

    private void assertDirectoryWasProperlyScanned(ScannerInput<SwitchYardModel> input) throws IOException {
        BeanSwitchYardScanner scanner = new BeanSwitchYardScanner();
    	SwitchYardModel _scannedModel = scanner.scan(input).getModel();
    	CompositeModel composite = _scannedModel.getComposite();
    	//30 components are expected to be found in the sample switchyard app
    	Assert.assertEquals(composite.getComponents().size(), 30);
    }
    
    
}
