import processing.core.PApplet;
import processing.core.PVector;
import java.util.Random;
import java.util.ArrayList;

class Projection {
    float K;
    Projection(float arbitaryContantK) {
        K = arbitaryContantK;
    }

    PVector matrixMultiply(float[][] a, float[][] b) {
        int rowA = a.length;
        int colA = a[0].length;
        int colB = b[0].length;
        float[][] product = new float[rowA][colB];
        for (int i = 0; i < rowA; i++) {
            for (int j = 0; j < colB; j++) {
                float result = 0;
                for (int k = 0; k < colA; k++) {
                    result += a[i][k] * b[k][j];
                }
                product[i][j] = result;
            }
        }
        return matToVec3(product);
    }
    PVector matToVec3(float[][] matrix) {
        PVector vector = new PVector();
        vector.x = matrix[0][0];
        vector.y = matrix[1][0];
        vector.z = 0;
        if (matrix.length > 2) {
            vector.z = matrix[2][0];
        }
        return vector;
    }

    float[][] pVcToMat(PVector vector) {
        float[][] mat = new float[3][1];
        mat[0][0] = vector.x;
        mat[1][0] = vector.y;
        mat[2][0] = vector.z;

        return mat;
    }
}

public class Main extends PApplet {
    ArrayList<PVector> PointsOnCircle = new ArrayList<PVector>();
    ArrayList<String> letter = new ArrayList<String>();
    float tau = 0;

    float[][] RotateY(float theta) {
        return new float[][]{
                {cos(theta), 0, sin(theta)},
                {0, 1, 0},
                {-sin(theta), 0, cos(theta)}
        };
    }

    float[][] RotateZ(float theta) {
        return new float[][]{
                {cos(theta), -sin(theta), 0},
                {sin(theta), cos(theta), 0},
                {0, 0, 1}
        };
    }

    float[][] RotateX(float theta) {
        return new float[][]{
                {1, 0, 0},
                {0, cos(theta), -sin(theta)},
                {0, sin(theta), cos(theta)}
        };
    }

    public void settings() {
        size(700, 700);
        float theta = 0;
        // since we are not inside a method that runs in some fps
        // appending in array list point that lies in circle (plotting points)
        float Radius = 80;
        float DistanceFromAxis = 150; // radius of TUBE
        float stepSize = TWO_PI / 50; // scaling for even distributions of point
        while (theta < TWO_PI) {
            float phi = 0;
            while (phi < TWO_PI) {
                // range of 0 to 2pi
                PVector calculatedSimplifiedFormula = new PVector();
                // wikipedia for parametric form of tours
                // derivation (R2, 0, 0) + (r cos x + r sin x)  where R2 = major length or length of tube
                // r = length of circle solve and multiply with rotation matrix along Y (I copied from wiki solved sol)
                // for further animation multiply with rotation z and rotation y or z with increment in angle
                // i.e increment 0.01 optimal in method which is frame rate
                calculatedSimplifiedFormula.x = (DistanceFromAxis + Radius * cos(theta)) * cos(phi);
                calculatedSimplifiedFormula.y = Radius * sin(theta);
                calculatedSimplifiedFormula.z = -(DistanceFromAxis + Radius * cos(theta)) * sin(phi);
                PointsOnCircle.add(calculatedSimplifiedFormula);
                Random r = new Random();
                char randomChar = (char) (r.nextInt(26) + 'a');
                letter.add(Character.toString(randomChar));
                phi += stepSize;
            }
            theta += stepSize;
        }
    }

    Projection projection = new Projection(25);

    public void draw() {
        translate(width / 2, height / 2);  // centers
        background(0);
        stroke(255);
        strokeWeight(4);
        noFill();

        for (int i = 0; i <= PointsOnCircle.size() - 1; i++) {
            // let's try to rotate each and every point
            float[][] coordinatesInMatrix = projection.pVcToMat(PointsOnCircle.get(i));
            PVector angularRotation = projection.matrixMultiply(RotateZ(tau), coordinatesInMatrix);
            angularRotation = projection.matrixMultiply(RotateX(tau), projection.pVcToMat(angularRotation));
            text(letter.get(i), angularRotation.x, angularRotation.y);
        }
        tau += 0.01;
    }


    public static void main(String[] args) {
        PApplet.main("Main", args);
    }
}