package hearc.ch.maraudermapapplication.tools.math;

/**
 * Calcul du filtre Kálmán : se reférer à Wikipédia
 *
 * Created by leonardo.distasio on 03.12.2015.
 */
public class Kalman
{
    private static final double SIGMA = 5;
    private static final int RAND_LOWER = 0;
    private static final int L = 1;

    private double[][] tabF;
    private double[] tabXPrevious;
    private double[] tabBk;
    private double[][] tabL;
    private double[][] tabQ;
    private double[][] tabPkPrevious;
    private double[] tabXkPrevious;
    private double[] tabKk;
    private double[] tabVk;
    private double dt;
    private double ak;

    public Kalman(double distance, double ak)
    {
        this.dt = 2.0;
        this.ak = ak;
        this.tabVk = new double[] {0, SIGMA};
        this.tabF = new double[][]{{1.0, this.dt}, {0.0, 1.0}};
        this.tabL = new double[][]{{L, 0.0}, {0.0, L}};
        this.tabXPrevious = new double[]{distance, distance};
        this.tabBk = new double[]{(Math.pow(this.dt, 2) / 2), this.dt};

        this.tabQ = new double[][] {
                {Math.pow(this.dt, 4) / 4, Math.pow(this.dt, 3) / 2},
                {Math.pow(this.dt, 3) / 2, Math.pow(this.dt, 2)}
        };

    }

    public double calculateKalman()
    {
        this.tabXkPrevious = calculateXkPrevious();
        this.tabPkPrevious = calculatePkPrevious();
        this.tabKk = calculateKk();

        double yTilde = calculateYTilde();

        double[] matriceXkPreviousKk = Matrice.addMatrice(this.tabXkPrevious, this.tabKk);

        return Matrice.mulMatrice(matriceXkPreviousKk, yTilde)[0];
    }

    private double[] calculateXkPrevious()
    {
        double[] matFMulXk = Matrice.mulMatrice(this.tabF, this.tabXPrevious);
        this.ak = SIGMA * Math.sqrt(2) * this.ak;
        return Matrice.addMatrice(matFMulXk, this.gaussianNoise());
    }

    private double calculateZk() {
        double[] matriceXkVk = Matrice.addMatrice(this.tabXkPrevious, this.tabVk);

        return matriceXkVk[0];
    }

    private double calculateYTilde()
    {
        double zk = calculateZk();
        double HkMulXk = calculateXkPrevious()[0];

        return zk - HkMulXk;
    }

    private double[][] calculatePkPrevious()
    {
        double[][] transposeF = Matrice.transposeMatrice(this.tabF);
        double[][] mulFL = Matrice.mulMatrice(this.tabF, this.tabL);

        double[][] mulFLTransposeF = Matrice.mulMatrice(mulFL, transposeF);

        return Matrice.addMatrice(mulFLTransposeF, this.tabQ);
    }

    private double[] calculateKk()
    {
        double[] matKk = new double[2];

        double ek = this.tabPkPrevious[0][0], ck = this.tabPkPrevious[1][0];

        matKk[0] = ek / (ek + SIGMA * SIGMA);
        matKk[1] = ck / (ek + SIGMA * SIGMA);

        return matKk;
    }

    private double random() {
        return (Math.random() * (SIGMA - RAND_LOWER)) + RAND_LOWER;
    }

    private double[] gaussianNoise()
    {
        double[] newMatrice = new double[2];

        for (int i = 0; i < 2; i++)
        {
            newMatrice[i] = this.tabBk[i] * this.ak;
        }

        return newMatrice;
    }
}
