package com.hbm.nucleartech.explosion;

public class ShapeCalculator {
    public static int secInRange(int dcx, int dcy, int dcz, int xinsec, int yinsec, int zinsec, int radx, int rady, int edge, VeryFastRaycastedExplosion.Shape shape) //corner calculation
    {
        int dx, dy, dz, closeinrange, farinrange;
        //get furthest corner
        if (dcx == 0)
            dx = Math.max(xinsec, 15 - xinsec);
        else if (dcx > 0)
            dx = (dcx << 4) + 15 - xinsec;
        else
            dx = (dcx << 4) - xinsec;
        if (dcy == 0)
            dy = Math.max(yinsec, 15 - yinsec);
        else if (dcy > 0)
            dy = (dcy << 4) + 15 - yinsec;
        else
            dy = (dcy << 4) - yinsec;
        if (dcz == 0)
            dz = Math.max(zinsec, 15 - zinsec);
        else if (dcz > 0)
            dz = (dcz << 4) + 15 - zinsec;
        else
            dz = (dcz << 4) - zinsec;
        farinrange = isInRange(dx, dy, dz, radx, rady, edge, shape); //needs to be inside edge, so only 2
        if (farinrange > 1) return 2; //furthest point in range, inside
        if (dcx == 0)
            dx = 0;
        else if (dcx > 0)
            dx = (dcx << 4) - xinsec;
        else
            dx = (dcx << 4) + 15 - xinsec;
        if (dcy == 0)
            dy = 0;
        else if (dcy > 0)
            dy = (dcy << 4) - yinsec;
        else
            dy = (dcy << 4) + 15 - yinsec;
        if (dcz == 0)
            dz = 0;
        else if (dcz > 0)
            dz = (dcz << 4) - zinsec;
        else
            dz = (dcz << 4) + 15 - zinsec;
        closeinrange = isInRange(dx, dy, dz, radx, rady, edge, shape); //inside edge
        if (closeinrange < 1) return 0; //closest point not in range, fully outside
        else return 1; //edge
    }

    public static int secInRangeEffect(int dcx, int dcy, int dcz, int xinsec, int yinsec, int zinsec,
                                       int minradx, int minrady, int radx, int rady, int edge, ExplosionEffect.EffectShape shape) //corner calculation
    {
        int dx, dy, dz, closeinrange, farinrange;
        //get furthest corner
        if (dcx == 0)
            dx = Math.max(xinsec, 15 - xinsec);
        else if (dcx > 0)
            dx = (dcx << 4) + 15 - xinsec;
        else
            dx = (dcx << 4) - xinsec;
        if (dcy == 0)
            dy = Math.max(yinsec, 15 - yinsec);
        else if (dcy > 0)
            dy = (dcy << 4) + 15 - yinsec;
        else
            dy = (dcy << 4) - yinsec;
        if (dcz == 0)
            dz = Math.max(zinsec, 15 - zinsec);
        else if (dcz > 0)
            dz = (dcz << 4) + 15 - zinsec;
        else
            dz = (dcz << 4) - zinsec;
        farinrange = isInRangeEffect(dx, dy, dz, minradx, minrady, radx, rady, edge, shape); //needs to be inside edge, so only 2
        if (farinrange > 1) return 2; //inside
        if (dcx == 0)
            dx = 0;
        else if (dcx > 0)
            dx = (dcx << 4) - xinsec;
        else
            dx = (dcx << 4) + 15 - xinsec;
        if (dcy == 0)
            dy = 0;
        else if (dcy > 0)
            dy = (dcy << 4) - yinsec;
        else
            dy = (dcy << 4) + 15 - yinsec;
        if (dcz == 0)
            dz = 0;
        else if (dcz > 0)
            dz = (dcz << 4) - zinsec;
        else
            dz = (dcz << 4) + 15 - zinsec;
        closeinrange = isInRangeEffect(dx, dy, dz, minradx, minrady, radx, rady, edge, shape); //inside edge
        if (closeinrange < 1) return 0; //fully outside
        else return 1; //edge
    }

    public static long distanceSq(long dx, long dy, long dz, int radx, int rady, VeryFastRaycastedExplosion.Shape shape) {
        return switch (shape) {
            case SPHERE -> dx*dx + dy*dy + dz*dz;
            case CUBOID -> Math.max(dx*dx/4, Math.max(dy*dy/4, dz*dz/4));
            case CYLINDER -> dx*dx + dz*dz;
            case ELLIPSOID -> dx*dx + dy*dy*radx/rady*radx/rady + dz*dz;
        };
    }

    public static long distanceSq(long dx, long dy, long dz, int radx, int rady, ExplosionEffect.EffectShape shape) {
        return switch (shape) {
            case SPHERE, DISTANCE_SPHERE, HOLLOW_SPHERE -> distanceSq(dx, dy, dz, radx, rady, VeryFastRaycastedExplosion.Shape.SPHERE);
            case CUBOID, DISTANCE_CUBOID, HOLLOW_CUBOID -> distanceSq(dx, dy, dz, radx, rady, VeryFastRaycastedExplosion.Shape.CUBOID);
            case CYLINDER, DISTANCE_CYLINDER, HOLLOW_CYLINDER -> distanceSq(dx, dy, dz, radx, rady, VeryFastRaycastedExplosion.Shape.CYLINDER);
            case ELLIPSOID, DISTACNE_ELLIPSOID, HOLLOW_ELLIPSOID -> distanceSq(dx, dy, dz, radx, rady, VeryFastRaycastedExplosion.Shape.ELLIPSOID);
        };
    }

    public static int isInRange(long dx, long dy, long dz, int radx, int rady, int edge, VeryFastRaycastedExplosion.Shape shape) {
        return switch (shape) {
            case SPHERE -> isInRangeIrrSphere(dx, dy, dz, radx, radx-edge);
            case CUBOID -> isInRangeIrrCuboid(dx, dy, dz, radx, rady, edge);
            case CYLINDER -> isInRangeIrrCylinder(dx, dy, dz, radx, rady, radx-edge);
            case ELLIPSOID -> isInRangeIrrEllipsoid(dx, dy, dz, radx, rady, radx-edge);
        };
    }

    public static boolean isInRangeNoEdge(long dx, long dy, long dz, int radx, int rady, int edge, VeryFastRaycastedExplosion.Shape shape) {
        return switch (shape) {
            case SPHERE -> isInRangeSphere(dx, dy, dz, radx);
            case CUBOID -> isInRangeCuboid(dx, dy, dz, radx, rady);
            case CYLINDER -> isInRangeCylinder(dx, dy, dz, radx, rady);
            case ELLIPSOID -> isInRangeEllipsoid(dx, dy, dz, radx, rady);
        };
    }

    public static int isInRangeEffect(long dx, long dy, long dz, int minradx, int minrady, int radx, int rady, int edge, ExplosionEffect.EffectShape shape) {
        return switch (shape) {
            case SPHERE, DISTANCE_SPHERE -> isInRangeIrrSphere(dx, dy, dz, radx, minradx);
            case HOLLOW_SPHERE -> isInRangeHollowSphere(dx, dy, dz, minradx, radx, radx-Math.abs(edge), minradx-Math.min(0, edge));
            case ELLIPSOID, DISTACNE_ELLIPSOID -> isInRangeIrrEllipsoid(dx, dy, dz, radx, rady, minradx);
            case HOLLOW_ELLIPSOID -> isInRangeHollowEllipsoid(dx, dy, dz, minradx, radx, rady, radx-Math.abs(edge), minradx-Math.min(0, edge));
            case CUBOID, DISTANCE_CUBOID -> isInRangeIrrCuboid(dx, dy, dz, radx, rady, edge);
            case HOLLOW_CUBOID -> isInRangeHollowCuboid(dx, dy, dz, minradx, minrady, radx, rady, radx-Math.abs(edge), rady-Math.abs(edge), minradx-Math.min(0, edge),minrady-Math.min(0, edge));
            case CYLINDER, DISTANCE_CYLINDER -> isInRangeIrrCylinder(dx, dy, dz, radx, rady, minradx);
            case HOLLOW_CYLINDER -> isInRangeHollowCylinder(dx, dy, dz, minradx, minrady, radx, rady, radx-Math.abs(edge), rady-Math.abs(edge), minradx-Math.min(0, edge), minrady-Math.min(0, edge));
        };
    }

    public static boolean chunkFullyInRange(int dcx, int dcz, int xinsec, int y, int zinsec, int minHeight, int maxHeight,
                                            int radx, int rady, int edge, VeryFastRaycastedExplosion.Shape shape) {
        return (secInRange(dcx, (minHeight-y)>>4, dcz, xinsec, y&15, zinsec, radx, rady, edge, shape) == 2 &&
        secInRange(dcx, (maxHeight-y)>>4, dcz, xinsec, y&15, zinsec, radx, rady, edge, shape) == 2);
    }


    public static boolean chunkInRange(int dcx, int dcz, int xinsec, int y, int zinsec, int minHeight, int maxHeight,
                                            int radx, int rady, int edge, VeryFastRaycastedExplosion.Shape shape) {
        int dcy = Math.min(Math.max(0, (minHeight-y)>>4), (maxHeight-y)>>4);
        return (secInRange(dcx, dcy, dcz, xinsec, y&15, zinsec, radx, rady, edge, shape) > 0);
    }

    public static boolean isInRangeSphere(long dx, long dy, long dz, int radx) {
        return distanceSq(dx, dy, dz, radx, radx, VeryFastRaycastedExplosion.Shape.SPHERE) < (long) radx*radx;
    }

    public static int isInRangeIrrSphere(long dx, long dy, long dz, int radx, int minradx) {
        long dist = distanceSq(dx, dy, dz, radx, radx, VeryFastRaycastedExplosion.Shape.SPHERE);
        if (dist >= (long) radx*radx) return 0; //outside
        else if (dist >= (long) minradx*minradx) return 1; //edge
        else return 2; //inside
    }

    public static int isInRangeHollowSphere(long dx, long dy, long dz, int minradx, int radx,
                                            int maxwidthedge, int minwidthedge) {
        long dist = distanceSq(dx, dy, dz, radx, radx, VeryFastRaycastedExplosion.Shape.SPHERE);
        if (dist < (long) minradx*minradx) return 4; //inside
        if (dist < (long) minwidthedge*minwidthedge) return 3; //inner edge
        if (dist < (long) maxwidthedge*maxwidthedge) return 2; //perfect
        if (dist < (long) radx*radx) return 1; //outer edge
        return 0; //outside
    }

    public static boolean isInRangeEllipsoid(long dx, long dy, long dz, int radx, int rady) {
        return distanceSq(dx, dy, dz, radx, radx, VeryFastRaycastedExplosion.Shape.ELLIPSOID) < (long) radx*radx;
    }

    public static int isInRangeIrrEllipsoid(long dx, long dy, long dz, int radx, int rady, int minradx) {
        long dist = distanceSq(dx, dy, dz, radx, radx, VeryFastRaycastedExplosion.Shape.ELLIPSOID);
        if (dist >= (long) radx*radx) return 0; //outside
        else if (dist >= (long) minradx*minradx) return 1; //edge
        else return 2; //inside
    }

    public static int isInRangeHollowEllipsoid(long dx, long dy, long dz, int minradx, int radx, int rady,
                                               int maxwidthedge, int minwidthedge) {
        long dist = distanceSq(dx, dy, dz, radx, rady, VeryFastRaycastedExplosion.Shape.ELLIPSOID);
        if (dist < (long) minradx*minradx) return 4; //inside
        if (dist < (long) minwidthedge*minwidthedge) return 3; //inner edge
        if (dist < (long) maxwidthedge*maxwidthedge) return 2; //perfect
        if (dist < (long) radx*radx) return 1; //outer edge
        return 0; //outside
    }

    public static boolean isInRangeCuboid(long dx, long dy, long dz, int width, int height) {
        return dx <= (width)/2 && dx > (width+1)/-2 && dy <= (height)/2 && dy > (height+1)/-2 && dz <= (width)/2 && dz > (width+1)/-2;
    }

    public static int isInRangeIrrCuboid(long dx, long dy, long dz, int width, int height, int edge) {
        if (dx > (width)/2      || dx <= (width+1)/-2      || dy > (height)/2      || dy <= (height+1)/-2      || dz > (width)/2      || dz <= (width+1)/-2) return 0; //outside
        if (dx > (width)/2-edge || dx <= (width+1)/-2+edge || dy > (height)/2-edge || dy <= (height+1)/-2+edge || dz > (width)/2-edge || dz <= (width+1)/-2+edge) return 1;// edge
        else return 2; //inside
    }

    public static int isInRangeHollowCuboid(long dx, long dy, long dz, int minwidth, int minheight, int width, int height,
                                            int maxwidthedge, int maxheightedge, int minwidthedge, int minheightedge) {
        dx = Math.abs(dx);
        dy = Math.abs(dy);
        dz = Math.abs(dz);
        if (dx < minwidth && dy < minheight && dz < minwidth) return 4; //inside
        if (dx < minwidthedge && dy < minheightedge && dz < minwidthedge) return 3; //inner edge
        if (dx < maxwidthedge && dy < maxheightedge && dz < maxwidthedge) return 2; //perfect
        if (dx < width && dy < height && dz < width) return 1; //outer edge
        return 0; //outside
    }

    public static boolean isInRangeCylinder(long dx, long dy, long dz, int radx, int height) {
        return dx * dx + dz * dz < (long) radx*radx && dy <= (height)/2 && dy > (height+1)/-2;
    }

    public static int isInRangeIrrCylinder(long dx, long dy, long dz, int radx, int height, int minradx) {
        if (dx * dx + dz * dz > (long) radx*radx || dy > (height)/2 || dy <= (height+1)/-2) return 0; //outside
        else if (dx * dx + dz * dz > (long) minradx*minradx) return 1; //edge
        else return 2; //inside
    }

    public static int isInRangeHollowCylinder(long dx, long dy, long dz, int minradx, int minheight, int radx, int height,
                                              int maxwidthedge, int maxheightedge, int minwidthedge, int minheightedge) {
        long dist = distanceSq(dx, dy, dz, radx, height, VeryFastRaycastedExplosion.Shape.CYLINDER);
        dy = Math.abs(dy);
        if (dist <= (long)minradx*minradx && dy <= minheight) return 4; //inside
        if (dist < (long) minwidthedge*minwidthedge && dy <= minheightedge) return 3; //inner edge
        if (dist < (long) maxwidthedge*maxwidthedge && dy <= maxheightedge) return 2; //perfect
        if (dist < (long) radx*radx && dy <= height) return 1; //outer edge
        return 0; //outside
    }

}
