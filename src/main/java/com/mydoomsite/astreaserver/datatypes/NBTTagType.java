package com.mydoomsite.astreaserver.datatypes;

public enum NBTTagType
{
	End			(0),
	Byte		(1),
	Short		(2),
	Int			(3),
	Long		(4),
	Float		(5),
	Double		(6),
	ByteArray	(7),
	String		(8),
	List		(9),
	Compound	(10),
	IntArray	(11),
	LongArray	(12);
	
	private final int id;
	
	private NBTTagType(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
}
