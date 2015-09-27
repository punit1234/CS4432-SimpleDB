package simpledb.buffer;

import java.util.HashMap;

import simpledb.file.*;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * 
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {
	private Buffer[] bufferpool;
	private int numAvailable;
	//CS4432-Project1:
	//A Hashmap with Key blockID and value Index to Buffer Pool
	private HashMap<Integer, Integer> mapIDtoIndex;
	private String replacementPolicy = "Clock";

	/**
	 * Creates a buffer manager having the specified number of buffer slots.
	 * This constructor depends on both the {@link FileMgr} and
	 * {@link simpledb.log.LogMgr LogMgr} objects that it gets from the class
	 * {@link simpledb.server.SimpleDB}. Those objects are created during system
	 * initialization. Thus this constructor cannot be called until
	 * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or is called
	 * first.
	 * 
	 * @param numbuffs
	 *            the number of buffer slots to allocate
	 */
	BasicBufferMgr(int numbuffs) {
		bufferpool = new Buffer[numbuffs];
		numAvailable = numbuffs;
		mapIDtoIndex = new HashMap<Integer, Integer>(numbuffs);

		for (int i = 0; i < numbuffs; i++) {
			Buffer b = new Buffer();
			b.setPoolIndex(i);
			bufferpool[i] = b;
		}
	}

	/**
	 * Flushes the dirty buffers modified by the specified transaction.
	 * 
	 * @param txnum
	 *            the transaction's id number
	 */
	synchronized void flushAll(int txnum) {
		for (Buffer buff : bufferpool)
			if (buff.isModifiedBy(txnum))
				buff.flush();
	}

	/**
	 * Pins a buffer to the specified block. If there is already a buffer
	 * assigned to that block then that buffer is used; otherwise, an unpinned
	 * buffer from the pool is chosen. Returns a null value if there are no
	 * available buffers.
	 * 
	 * @param blk
	 *            a reference to a disk block
	 * @return the pinned buffer
	 */
	synchronized Buffer pin(Block blk) {
		Buffer buff = findExistingBuffer(blk);
		if (buff == null) {
			buff = chooseUnpinnedBuffer();
			if (buff == null)
				return null;
			buff.assignToBlock(blk);
			//CS4432-Project1:
			//Set second chance bit
			buff.setSecondChance(true);
			mapIDtoIndex.put(buff.block().hashCode(), buff.getPoolIndex());

		}
		if (!buff.isPinned())
			numAvailable--;
		buff.pin();
		return buff;
	}

	/**
	 * Allocates a new block in the specified file, and pins a buffer to it.
	 * Returns null (without allocating the block) if there are no available
	 * buffers.
	 * 
	 * @param filename
	 *            the name of the file
	 * @param fmtr
	 *            a pageformatter object, used to format the new block
	 * @return the pinned buffer
	 */
	synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
		Buffer buff = chooseUnpinnedBuffer();
		if (buff == null)
			return null;
		buff.assignToNew(filename, fmtr);
		numAvailable--;
		buff.pin();
		//CS4432-Project1:
		//Set second chance bit & put in hashmap
		buff.setSecondChance(true);
		mapIDtoIndex.put(buff.block().hashCode(), buff.getPoolIndex());

		return buff;
	}

	/**
	 * Unpins the specified buffer.
	 * 
	 * @param buff
	 *            the buffer to be unpinned
	 */
	synchronized void unpin(Buffer buff) {
		buff.unpin();
		if (!buff.isPinned())
			numAvailable++;
	}

	/**
	 * Returns the number of available (i.e. unpinned) buffers.
	 * 
	 * @return the number of available buffers
	 */
	int available() {
		return numAvailable;
	}

	private Buffer findExistingBuffer(Block blk) {
		// for (Buffer buff : bufferpool) {
		// Block b = buff.block();
		// if (b != null && b.equals(blk))
		// return buff;
		// }
		// return null;
		Integer i = mapIDtoIndex.get(blk.hashCode());
		if(i != null)	
			System.out.println("Block ID " + blk.number()+ " exists at Index "+  i.toString() + " of the buffer pool");
		// if it doesnt exist
		else {
			return null;
		}
		Buffer b = bufferpool[i];

		return b;
	}
	/**
	 * CS4432-Project1:
	 *  
	 *  Clock hand Variable pointer;
	 *  Initially pointing at the head of the Buffer Pool Array
	 * 
	 * 
	 */
	 int clockHand = 0;

	private Buffer chooseUnpinnedBuffer() {
		// 2.1
		// CHECK FOR EMPTY FRAME FIRST;

		for (Buffer buff : bufferpool)
			if (buff.block() == null)
				return buff;
		
		
		
		//2.3 Efficient Replacement Policy Clock
		if (replacementPolicy.equals("Clock")) {
			while (true) {
				Buffer buff = bufferpool[clockHand];
				if (!buff.isPinned()) {
					if (buff.isSecondChance()){
						System.out.println("Buffer at clockhand "+ clockHand + " has a second chance.");
						buff.setSecondChance(false);
					}
					else {
						System.out.println("Buffer at clockhand "+ clockHand + " is being removed.");

						mapIDtoIndex.remove(buff.block().hashCode());
						return buff;
					}

				}
				clockHand++;
				if (clockHand == bufferpool.length) {
					clockHand = 0;
				}
			}
		} else {
			// regular iterative search through to find a usable buffer

			for (Buffer buff : bufferpool) {
				// if the buffer is empty
				if (buff.block() == null)
					return buff;

				// if the buffer isnt empty
				if (!buff.isPinned()) {
					mapIDtoIndex.remove(buff.block().hashCode());
					return buff;

				}
			}
		}
		return null;

	}
	/**
	 * CS4432-Project1:
	 * 2.5 Report Functions
	 * 
	 * Iterates through the buffer pool and prints out each buffer's relevant information
	 */
	@Override public String toString(){
	StringBuilder sb = new StringBuilder();
	int i = 0;
	for (Buffer b : bufferpool){
		sb.append(i + ":\n");
		sb.append(b.toString() + "\n");
	}
	return sb.toString();
}
}
