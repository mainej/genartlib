(ns genartlib.geometry
  (:use [genartlib.algebra :only [angle avg interpolate]])
  (:use [genartlib.util :only [between?]])
  (:use [quil.core :only [dist cos sin]])
  (:import [genartlib PolyUtils]))

  (defn rotate-polygon [theta points]
    "Rotates a polygon clockwise about its centroid.  The theta argument determines
     how the polygon is rotated.  `points` is a sequence of [x y] pairs that
     define the polygon"
    (if (zero? theta)
      points
      (let [xs (map first points)
            ys (map second points)
            x-centroid (/ (apply + xs) (count xs))
            y-centroid (/ (apply + ys) (count ys))
            points (map vector xs ys)]
        (map (fn [[x y]]
               (let [current-angle (angle x-centroid y-centroid x y)
                     new-angle (+ current-angle theta)
                     hypot (dist x y x-centroid y-centroid)
                     x-offset (* hypot (cos new-angle))
                     y-offset (* hypot (sin new-angle))]
                 [(+ x-offset x-centroid) (+ y-offset y-centroid)]))
               points))))

  (defn polygon-contains-point? [x-verts y-verts test-x test-y]
    "Returns true if a polygon contains the given point.  The polygon
     is defined by the first two arguments: a sequence of x values
     and a sequence of y values that define the polygon."
    (PolyUtils/polygonContainsPoint (double-array x-verts) (double-array y-verts) (double test-x) (double test-y)))

  (defn shrink-polygon [points ratio]
    (let [x-points (map first points)
          y-points (map second points)

          x-centroid (apply avg x-points)
          y-centroid (apply avg y-points)]
      (for [[x y] points]
        [(interpolate x x-centroid (- 1.0 ratio))
         (interpolate y y-centroid (- 1.0 ratio))])))
