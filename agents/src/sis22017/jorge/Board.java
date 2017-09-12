package sis22017.jorge;

public class Board
{
	private byte[][] instance;

	public Board( byte[][] instance )
	{
		this.instance = instance;
	}

	public byte[][] getInstance()
	{
		return instance;
	}

	public void setInstance( byte[][] instance )
	{
		this.instance = instance;
	}

	@Override
	public int hashCode()
	{
		int prime = 31;
		int result = 1;

		for( int i = 0; i < this.instance.length; i++ )
			for( int j = 0; j < this.instance[i].length; j++ )
				result = prime * result + this.instance[i][j];

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

		Board other = ( Board ) object;

		for( int i = 0; i < this.instance.length; i++ )
			for( int j = 0; j < this.instance[i].length; j++ )
				if( this.instance[i][j] != other.getInstance()[i][j] )
					return false;

		return true;
	}
}