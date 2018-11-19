package blockchain.learning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkWeb {
	private static final Logger LOGGER = LoggerFactory.getLogger(SparkWeb.class);
	private static Gson gson = new Gson();
	private List<Block> blockChain = new ArrayList<Block>();

	public List<Block> getBlockChain() {
		return blockChain;
	}

	public void setBlockChain(List<Block> blockChain) {
		this.blockChain = blockChain;
	}

	public static void main(String[] args) {
		SparkWeb web = new SparkWeb();
		Block genesisBlock = new Block();
		genesisBlock.setIndex(0);
		genesisBlock.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		genesisBlock.setVac(0);
		genesisBlock.setPrevHash("");
		genesisBlock.setHash(calculateHash(genesisBlock));
		web.getBlockChain().add(genesisBlock);
		Route route1 = web.new Get_RetriveAllBlockChain();
		Spark.get("/", route1);
		LOGGER.info("route1 is running...");
		Route route2 = web.new Post_CreateBlock();
		Spark.post("/", route2);
		LOGGER.info("route2 is running...");
	}

	public static String calculateHash(Block block) {
		String record = (block.getIndex()) + block.getTimestamp() + (block.getVac()) + block.getPrevHash();
		return SHA256.crypt(record);
	}

	public static Block generateBlock(Block oldBlock, int vac) {
		Block newBlock = new Block();
		newBlock.setIndex(oldBlock.getIndex() + 1);
		newBlock.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		newBlock.setVac(vac);
		newBlock.setPrevHash(oldBlock.getHash());
		newBlock.setHash(calculateHash(newBlock));
		return newBlock;
	}

	public static boolean isBlockValid(Block newBlock, Block oldBlock) {
		if (oldBlock.getIndex() + 1 != newBlock.getIndex()) {
			return false;
		}
		if (!oldBlock.getHash().equals(newBlock.getPrevHash())) {
			return false;
		}
		if (!calculateHash(newBlock).equals(newBlock.getHash())) {
			return false;
		}
		return true;
	}

	public void replaceChain(List<Block> newBlocks) {
		if (newBlocks.size() > blockChain.size()) {
			blockChain = newBlocks;
		}
	}

	private class Get_RetriveAllBlockChain implements Route {
		public Object handle(Request request, Response response) throws Exception {
			response.header("Content-Type", "text/json");
			return gson.toJson(blockChain);
		}
		
	}

	private class Post_CreateBlock implements Route {
		public Object handle(Request request, Response response) throws Exception {
			String body = request.body();
			Message m = gson.fromJson(body, Message.class);
			if (m == null) {
				return "vac is NULL";
			}
			int vac = m.getVac();
			Block lastBlock = blockChain.get(blockChain.size() - 1);
			Block newBlock = generateBlock(lastBlock, vac);
			if (isBlockValid(newBlock, lastBlock)) {
				blockChain.add(newBlock);
				LOGGER.debug(gson.toJson(blockChain));
			} else {
				return "HTTP 500: Invalid Block Error";
			}
			return "success!";
		}

	}

}

class Message {
	private int vac;

	public int getVac() {
		return vac;
	}

	public void setVac(int vac) {
		this.vac = vac;
	}

}