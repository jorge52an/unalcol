package sis22017.jorge;

public class Node
{
	private Node parent;
	private Board board;

	public Node( Node parent, Board board )
	{
		this.parent = parent;
		this.board = board;
	}

	public Node getParent()
	{
		return parent;
	}

	public Board getBoard()
	{
		return board;
	}
}
