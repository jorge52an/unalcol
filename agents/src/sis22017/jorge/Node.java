package sis22017.jorge;

public class Node
{
	private Node parent;
	private Board board;
	private int cost;

	public Node( Node parent, Board board, int cost )
	{
		this.parent = parent;
		this.board = board;
		this.cost = cost;
	}

	private int getHeuristicCost()
	{
		return 0;
	}

	public Node getParent()
	{
		return parent;
	}

	public Board getBoard()
	{
		return board;
	}

	public int getCost()
	{
		return this.cost + this.getHeuristicCost();
	}
}
