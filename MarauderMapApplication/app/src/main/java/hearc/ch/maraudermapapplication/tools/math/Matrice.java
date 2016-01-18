package hearc.ch.maraudermapapplication.tools.math;

/**
 * Calcul élémentaire de plusieurs matrices
 *
 * Created by leonardo.distasio on 03.12.2015.
 */
public class Matrice
{

    public Matrice()
    {
    }

    public static double[][] addMatrice(double[][] matrice, double[][] matrice2)
    {
        int row = 2, col = 2;
        if(row != matrice2.length)
            return null;

        double[][] newMatrice = new double[row][col];

        for(int i = 0; i < matrice.length; i++)
        {
            for (int j = 0; j < matrice[i].length; j++)
            {
                newMatrice[i][j] = matrice[i][j] + matrice2[i][j];
            }
        }

        return newMatrice;
    }

    public static double[] addMatrice(double[] matrice, double[] matrice2)
    {
        int row = 2;
        if(row != matrice2.length)
            return null;

        double[] newMatrice = new double[row];

        for(int i = 0; i < matrice.length; i++)
        {
            newMatrice[i] = matrice[i] + matrice2[i];
        }

        return newMatrice;
    }

    public static double[][] mulMatrice(double[][] matrice, double[][] matrice2)
    {
        int row = 2, col = 2;
        double[][] newMatrice = new double[row][col];

        if(matrice2.length != row)
            return null;

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                for (int k = 0; k < col; k++)
                {
                    newMatrice[i][j] += matrice[i][k] * matrice2[k][j];
                }
            }
        }

        return newMatrice;
    }

    public static double[][] transposeMatrice(double[][] matrice)
    {
        int row = 2, col = 2;
        double[][] newMatrice = new double[row][col];

        for(int i = 0; i < matrice.length; i++)
        {
            for (int j = 0; j < matrice[i].length; j++)
            {
                newMatrice[j][i] = matrice[i][j];
            }
        }

        return newMatrice;
    }

    public static double[] mulMatrice(double[][] matrice, double[] matrice2)
    {
        int row = 2, col = 2;
        double[] newMatrice = new double[row];

        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                for (int k = 0; k < col; k++)
                {
                    newMatrice[i] += matrice[i][k] * matrice2[k];
                }
            }
        }

        return newMatrice;
    }

    public static String printMatrice(double[][] matrice)
    {
        String str = "{";

        for(int i = 0; i < matrice.length; i++)
        {
            str += "{";
            for (int j = 0; j < matrice[i].length; j++)
            {
                str += matrice[i][j] + " ";
            }
            str += "}";
        }

        str += "}";
        return str;
    }

    public static String printMatrice(double[] matrice)
    {
        String str = "{";
        for(int i = 0; i < matrice.length; i++)
        {
            str += matrice[i] + " ";
        }
        str += "}";
        return str;
    }

    public static double[] mulMatrice(double[] matriceXkPreviousKk, double yTilde)
    {
        for(int i = 0; i < matriceXkPreviousKk.length; i++)
        {
            matriceXkPreviousKk[i] *= yTilde;
        }

        return matriceXkPreviousKk;
    }
}
