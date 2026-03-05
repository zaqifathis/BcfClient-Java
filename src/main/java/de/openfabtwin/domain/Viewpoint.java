package de.openfabtwin.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * Viewpoint — domain model, deserializes directly from JSON response.
 *
 * Nested objects (camera, lines, etc.) are inner classes —
 * they stay inside Viewpoint, not exposed as separate files.
 */
@Data
public class Viewpoint {

    @JsonProperty("guid")
    private String guid;

    @JsonProperty("index")
    private Integer index;

    @JsonProperty("perspective_camera")
    private PerspectiveCamera perspectiveCamera;

    @JsonProperty("orthogonal_camera")
    private OrthogonalCamera orthogonalCamera;

    @JsonProperty("lines")
    private List<Line> lines;

    @JsonProperty("clipping_planes")
    private List<ClippingPlane> clippingPlanes;

    @JsonProperty("bitmaps")
    private List<Bitmap> bitmaps;

    @JsonProperty("snapshot")
    private Snapshot snapshot;

    // -------------------------------------------------------------------------
    // Inner classes — nested objects in JSON
    // -------------------------------------------------------------------------

    @Data
    public static class PerspectiveCamera {
        @JsonProperty("camera_view_point") private Point cameraViewPoint;
        @JsonProperty("camera_direction")  private Point cameraDirection;
        @JsonProperty("camera_up_vector")  private Point cameraUpVector;
        @JsonProperty("field_of_view")     private Double fieldOfView;
        @JsonProperty("aspect_ratio")      private Double aspectRatio;
    }

    @Data
    public static class OrthogonalCamera {
        @JsonProperty("camera_view_point")   private Point  cameraViewPoint;
        @JsonProperty("camera_direction")    private Point  cameraDirection;
        @JsonProperty("camera_up_vector")    private Point  cameraUpVector;
        @JsonProperty("view_to_world_scale") private Double viewToWorldScale;
        @JsonProperty("aspect_ratio")        private Double aspectRatio;
    }

    @Data
    public static class Point {
        @JsonProperty("x") private Double x;
        @JsonProperty("y") private Double y;
        @JsonProperty("z") private Double z;
    }

    @Data
    public static class Line {
        @JsonProperty("start_point") private Point startPoint;
        @JsonProperty("end_point")   private Point endPoint;
    }

    @Data
    public static class ClippingPlane {
        @JsonProperty("location")  private Point location;
        @JsonProperty("direction") private Point direction;
    }

    @Data
    public static class Bitmap {
        @JsonProperty("guid")        private String  guid;
        @JsonProperty("bitmap_type") private String  bitmapType;
        @JsonProperty("location")    private Point   location;
        @JsonProperty("normal")      private Point   normal;
        @JsonProperty("up")          private Point   up;
        @JsonProperty("height")      private Integer height;
    }

    @Data
    public static class Snapshot {
        @JsonProperty("snapshot_type") private String snapshotType;
        @JsonProperty("snapshot_data") private String snapshotData; // base64, POST only
    }
}