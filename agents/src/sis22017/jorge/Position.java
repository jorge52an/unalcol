package sis22017.jorge;

public class Position
{
	private byte x;
	private byte y;

	public Position( int x, int y )
	{
		this.x = ( byte ) x;
		this.y = ( byte ) y;
	}

	public byte getX()
	{
		return x;
	}

	public byte getY()
	{
		return y;
	}

	@Override
	public int hashCode()
	{
		int prime = 31;
		int result = 1;

		result = prime * result + x;
		result = prime * result + y;

		return result;
	}

	@Override
	public boolean equals( Object object )
	{
		if( this == object )
			return true;
		if( object == null )
			return false;
		if( this.getClass() != object.getClass() )
			return false;

		Position other = (Position) object;

		return this.x == other.getX() && this.y == other.getY();
	}
}
