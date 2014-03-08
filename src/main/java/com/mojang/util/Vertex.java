package com.mojang.util;

public final class Vertex {

    public Vec3D vector;
    public float u;
    public float v;

    /**
     * Build a Vertex object with the vector coordinates and UV.
     * @param x
     * @param y
     * @param z
     * @param u
     * @param v
     */
    public Vertex(float x, float y, float z, float u, float v) {
        this(new Vec3D(x, y, z), u, v);
    }

    /**
     * Build a Vertex object with a prebuilt Vector3D object and UV.
     * @param vector
     * @param u
     * @param v
     */
    private Vertex(Vec3D vector, float u, float v) {
        this.vector = vector;
        this.u = u;
        this.v = v;
    }

    /**
     * Build a Vertex object with another Vertex object and UV.
     * @param vertex
     * @param u
     * @param v
     */
    private Vertex(Vertex vertex, float u, float v) {
        vector = vertex.vector;
        this.u = u;
        this.v = v;
    }

    /**
     * Build a Vertex object without specifying a coordinate set.
     * @param u
     * @param v
     * @return Vertex
     */
    public final Vertex create(float u, float v) {
        return new Vertex(this, u, v);
    }
}
