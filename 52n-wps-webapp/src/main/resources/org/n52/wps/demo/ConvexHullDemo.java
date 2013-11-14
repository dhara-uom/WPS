package org.n52.wps.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.n52.wps.io.GTHelper;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ConvexHullDemo extends AbstractSelfDescribingAlgorithm{


    public List<String> getInputIdentifiers(){

        List<String> list = new ArrayList();
        list.add("FEATURES");
        return list;

    }


    public List<String> getOutputIdentifiers(){

        List<String> list = new ArrayList();
        list.add("polygons");
        return list;

    }

    public Class getInputDataType(String identifier){

        if (identifier.equalsIgnoreCase("FEATURES")) {
            return GTVectorDataBinding.class;
        }
        return null;

    }
    public Class getOutputDataType(String identifier){

        if (identifier.equalsIgnoreCase("polygons")) {
            return GTVectorDataBinding.class;
        }
        return null;

    }


    public HashMap<String, IData> run(Map<String, List<IData>> inputData){

        if (inputData == null || !inputData.containsKey("FEATURES")) {
            throw new RuntimeException("Error while allocating input parameters");
        }
        List<IData> dataList = inputData.get("FEATURES");
        if (dataList == null || dataList.size() != 1) {
            throw new RuntimeException("Error while allocating input parameters");
        }

        IData firstInputData = dataList.get(0);
        FeatureCollection featureCollection = ((GTVectorDataBinding) firstInputData).getPayload();

        FeatureIterator iter = featureCollection.features();
        List<Coordinate> coordinateList = new ArrayList<Coordinate>();
        int counter = 0;
        Geometry unifiedGeometry = null;
        while (iter.hasNext()) {
            SimpleFeature feature = (SimpleFeature) iter.next();
            if (feature.getDefaultGeometry() == null) {
                throw new NullPointerException("defaultGeometry is null in feature id: "+ feature.getID());
            }
            Geometry geom = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coordinateArray = geom.getCoordinates();
            for(Coordinate coordinate : coordinateArray){
                coordinateList.add(coordinate);
            }
        }

        Coordinate[] coordinateArray = new Coordinate[coordinateList.size()];
        for(int i = 0; i<coordinateList.size(); i++){
            coordinateArray[i] = coordinateList.get(i);
        }

        ConvexHull convexHull = new ConvexHull(coordinateArray, new GeometryFactory());
        Geometry geometry = convexHull.getConvexHull();

        String uuid = UUID.randomUUID().toString();
        SimpleFeatureType featureType = GTHelper.createFeatureType(geometry, uuid, featureCollection.getSchema().getCoordinateReferenceSystem());
        GTHelper.createGML3SchemaForFeatureType(featureType);
        Feature feature = GTHelper.createFeature("0", geometry, featureType);

        FeatureCollection fOut = DefaultFeatureCollections.newCollection();
        fOut.add(feature);

        HashMap<String, IData> result = new HashMap<String, IData>();
        result.put("polygons", new GTVectorDataBinding(fOut));
        return result;



    }








}











