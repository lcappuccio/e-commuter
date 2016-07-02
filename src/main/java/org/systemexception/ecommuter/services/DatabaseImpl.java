package org.systemexception.ecommuter.services;

import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import org.neo4j.index.impl.lucene.LowerCaseKeywordAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.enums.DatabaseConfiguration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author leo
 * @date 02/07/16 23:23
 */
public class DatabaseImpl implements DatabaseApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Neo4jGraph graph;
	private Index<Vertex> index;
	private String dbFolder;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialSetup(String dbFolder) {
		this.dbFolder = dbFolder;
		graph = new Neo4jGraph(dbFolder);
		index = graph.createIndex(DatabaseConfiguration.VERTEX_INDEX.toString(), Vertex.class, new Parameter
				(DatabaseConfiguration.NEO_INDEX_PARAMETER.toString(), LowerCaseKeywordAnalyzer.class.getName()));
	}

	@Override
	public void addTerritories(String fileName) {
		throw new NotImplementedException();
	}
}
