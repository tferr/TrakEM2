/**
 * 
 */
package bunwarpj.trakem2.transform;

import bunwarpj.bUnwarpJImageModel;
import mpicbg.trakem2.transform.CoordinateTransform;

/**
 * @author ignacio
 *
 */
public class CubicBSplineTransform implements CoordinateTransform 
{
	private bUnwarpJImageModel swx;
	private bUnwarpJImageModel swy;
	private int intervals;
	private int width;
	private int height;
	
	/**
	 * Empty constructor 
	 */
	public CubicBSplineTransform(){}
	
	/**
	 * Cubic B-spline transform constructor
	 * 
	 * @param intervals intervals between B-spline coefficients
	 * @param cx B-spline coefficients for transformation in the x axis
	 * @param cy B-spline coefficients for transformation in the y axis
	 * @param width width of the target image
	 * @param height height of the target image
	 */
	public CubicBSplineTransform(int intervals, 
								  double[][]cx, 
								  double[][]cy,
								  int width,
								  int height)	
	{
		this.intervals = intervals;
		this.swx = new bUnwarpJImageModel(cx);
		this.swy = new bUnwarpJImageModel(cy);
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Cubic B-spline transform constructor
	 * 
	 * @param intervals intervals between B-spline coefficients
	 * @param swx B-spline model for transformation in the x axis
	 * @param swy B-spline model for transformation in the y axis
	 * @param width width of the target image
	 * @param height height of the target image
	 */
	public CubicBSplineTransform(int intervals, 
								  bUnwarpJImageModel swx, 
								  bUnwarpJImageModel swy,
								  int width,
								  int height)	
	{
		this.intervals = intervals;
		this.swx = swx;
		this.swy = swy;
		this.width = width;
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see mpicbg.models.CoordinateTransform#apply(float[])
	 */
	public float[] apply(final float[] l) 
	{
		final float[] w = l.clone();
		applyInPlace(w);
		return w;
	}

	/* (non-Javadoc)
	 * @see mpicbg.models.CoordinateTransform#applyInPlace(float[])
	 */
	public void applyInPlace(float[] l) 
	{
		// Compute the transformation mapping		
		final double tv = (double)(l[1] * intervals) / (double)(this.height - 1) + 1.0F;
		final double tu = (double)(l[0] * intervals) / (double)(this.width - 1) + 1.0F;
		
		l[0] = (float) swx.prepareForInterpolationAndInterpolateI(tu, tv, false, false);
		l[1] = (float) swy.prepareForInterpolationAndInterpolateI(tu, tv, false, false);			
	}


	/**
	 * Init cubic B-spline transform from the paramters of a string
	 * 
	 * @param dataString basic cubic B-spline transform parameters
	 */
	public void init(String dataString) throws NumberFormatException 
	{		
		// Read parameter between spaces		
		final String[] fields = dataString.split( "\\s+" );
		
		int j = 0;
		
		this.width = Integer.parseInt(fields[j++]);
		this.height = Integer.parseInt(fields[j++]);
		this.intervals = Integer.parseInt(fields[j++]);
		
		int size = (this.intervals + 3) * (this.intervals + 3);
			
		
		if (fields.length < (2*size + 3))
			throw new NumberFormatException( "Inappropriate parameters for " + this.getClass().getCanonicalName() );
		
		else
		{
			double[] cx = new double[size];
			for(int i = 0; i < size; i++)
				cx[i] = Double.parseDouble(fields[j++]);
			
			double[] cy = new double[size];
			for(int i = 0; i < size; i++)
				cy[i] = Double.parseDouble(fields[j++]);
			
			this.swx = new bUnwarpJImageModel(cx, size, size, 0);
			this.swy = new bUnwarpJImageModel(cy, size, size, 0);			
		}
			
	}


	/**
	 * Save cubic B-spline transform information into String
	 */
	public String toDataString() 
	{
		String text = new String(this.width + " " + this.height +  " " + intervals);
		
		final int size = (intervals + 3) * (intervals + 3);
		
		final double[] cx = this.swx.getCoefficients();
		
		for(int i = 0; i < size; i ++)
			text += " " + cx[i];
		
		final double[] cy = this.swy.getCoefficients();
		
		for(int i = 0; i < size; i ++)
			text += " " + cy[i];
		
		return text;
	}


	//@Override
	final public String toXML( final String indent )
	{
		return indent + "<ict_transform class=\"" + this.getClass().getCanonicalName() + "\" data=\"" + toDataString() + "\"/>";
	}
	
	/**
	 * Clone method
	 */
	final public CubicBSplineTransform clone()
	{
		CubicBSplineTransform transf = new CubicBSplineTransform();	
		transf.init( toDataString() );
		return transf;		
	}
	

}
