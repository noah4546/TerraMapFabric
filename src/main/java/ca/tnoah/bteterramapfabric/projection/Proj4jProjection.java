package ca.tnoah.bteterramapfabric.projection;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import lombok.Getter;
import net.buildtheearth.terraplusplus.config.GlobalParseRegistries;
import net.buildtheearth.terraplusplus.dep.com.fasterxml.jackson.annotation.JsonCreator;
import net.buildtheearth.terraplusplus.dep.com.fasterxml.jackson.annotation.JsonProperty;
import net.buildtheearth.terraplusplus.dep.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import org.osgeo.proj4j.*;

@JsonDeserialize
public class Proj4jProjection implements GeographicProjection {

    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

    private static final CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84", new String[] {
            "+proj=longlat",
            "+datum=WGS84",
            "+no_defs"
    });

    protected transient final CoordinateReferenceSystem targetCrs;

    private transient final CoordinateTransform toWgs;
    private transient final CoordinateTransform toTargetCrs;

    private final String name, param;

    public Proj4jProjection(CoordinateReferenceSystem crs) {
        this.targetCrs = crs;
        this.name = crs.getName();
        this.param = crs.getParameterString();
        this.toWgs = ctFactory.createTransform(targetCrs, WGS84);
        this.toTargetCrs = ctFactory.createTransform(WGS84, targetCrs);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public Proj4jProjection(
            @JsonProperty(value = "name", required = true) String crsName,
            @JsonProperty(value = "param", required = true) String crsParameter
    ) {
        this(crsFactory.createFromParameters(crsName, crsParameter));
    }

    @Override
    public double[] fromGeo(double longitude, double latitude) {
        ProjCoordinate result = new ProjCoordinate();
        toTargetCrs.transform(new ProjCoordinate(longitude, latitude), result);
        return new double[] {result.x, result.y};
    }

    @Override
    public double metersPerUnit() {
        return 0;
    }

    @Override
    public double[] toGeo(double x, double z) {
        ProjCoordinate result = new ProjCoordinate();
        toWgs.transform(new ProjCoordinate(x, z), result);
        return new double[] {result.x, result.y};
    }

    public static void registerProjection() {
        GlobalParseRegistries.PROJECTIONS.putIfAbsent("proj4", Proj4jProjection.class);
    }

    public String getName() {
        return name;
    }

    public String getParam() {
        return param;
    }

    static {
        registerProjection();
    }
}