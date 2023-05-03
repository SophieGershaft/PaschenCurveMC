/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

/**
 *
 * @author sgershaft
 */
public class Vector {

    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getNorm() {
        return Math.sqrt(this.Square());
    }

    public double Square() {
        return (x * x + y * y + z * z);
    }

    // only for PP
    public double getTransverseComponent() {
        return Math.sqrt(y * y + z * z);
    }
    
    public Vector mutateAddVector(Vector other) {
        // add a new vector to the current vector by adding components
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector addVectors(Vector other) {
        // add a new vector to the current vector by adding components
        double newX = this.x + other.x;
        double newY = this.y + other.y;
        double newZ = this.z + other.z;
        Vector newVector = new Vector(newX, newY, newZ);
        return newVector;
    }
    
    // MAKE MUTATE MULTIPLY BY SCALAR
    public Vector multiplyByScalar(double scalar) {
        // multiply this (current vector) by a scalar
        double newX = this.x * scalar;
        double newY = this.y * scalar;
        double newZ = this.z * scalar;
        Vector newVector = new Vector(newX, newY, newZ);
        return newVector;
    }
    
    // MAKE MUTATE GET UNIT VECTOR
    public Vector getUnitVector() {
        // multiply each component by 1/norm (1/magnitude)
        // keep direction, make norm = 1
        double norm = this.getNorm();
        double newX = this.x * (1/norm);
        double newY = this.y * (1/norm);
        double newZ = this.z * (1/norm);
        Vector newVector = new Vector(newX, newY, newZ);
        return newVector;
    }
    
    public Vector constructPerpendicular() {
        // perpendicular to original vector
        Vector v = new Vector(0, 0, 0);
        
        if (Math.abs(this.z) <= Math.abs(this.y) && Math.abs(this.z) <= Math.abs(this.x)) {
            // z is smallest entry, don't use it
            v.x = -this.y;
            v.y = this.x;
            v.z = 0.0;
        } else if (Math.abs(y) <= Math.abs(x)) {
            // y is smallest entry, don't use it
            v.x = -this.z;
            v.y = 0.0;
            v.z = this.x;
        } else {
            // x is smallest entry, don't use it
            v.x = 0.0;
            v.y = -this.z;
            v.z = this.y;
        }
        v = v.getUnitVector();
        return v;
    }
    
    public Vector getCrossProduct(Vector b) {
        Vector a = this;
        Vector v = new Vector(0, 0, 0);
        // cross product of vector v:
        // v.x = (a2 * b3 - a3 * b2)
        // v.y = (a3 * b1 - a1 * b3)
        // v.z = (a1 * b2 - a2 * b1)
        
        v.x = a.y*b.z - a.z*b.y;
        v.y = a.z*b.x - a.x*b.z;
        v.z = a.x*b.y - a.y*b.x;
        
        return v;
    }

}