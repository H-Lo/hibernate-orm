/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.spi;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sql.ast.SqlAstDeleteTranslator;
import org.hibernate.sql.ast.tree.delete.DeleteStatement;
import org.hibernate.sql.ast.tree.expression.ColumnReference;
import org.hibernate.sql.exec.spi.JdbcDelete;
import org.hibernate.sql.exec.spi.JdbcParameterBinder;

/**
 * @author Steve Ebersole
 */
public class StandardSqlAstDeleteTranslator
		extends AbstractSqlAstToJdbcOperationConverter
		implements SqlAstDeleteTranslator {
	public StandardSqlAstDeleteTranslator(SessionFactoryImplementor sessionFactory) {
		super( sessionFactory );
	}

	@Override
	public JdbcDelete translate(DeleteStatement sqlAst) {
		appendSql( "delete from " );
		appendSql( sqlAst.getTargetTable().getTableExpression() );

		if ( sqlAst.getRestriction() != null ) {
			appendSql( " where " );
			sqlAst.getRestriction().accept( this );
		}

		return new JdbcDelete() {
			@Override
			public String getSql() {
				return StandardSqlAstDeleteTranslator.this.getSql();
			}

			@Override
			public List<JdbcParameterBinder> getParameterBinders() {
				return StandardSqlAstDeleteTranslator.this.getParameterBinders();
			}

			@Override
			public Set<String> getAffectedTableNames() {
				return getAffectedTableExpressions();
			}
		};
	}

	@Override
	public void visitColumnReference(ColumnReference columnReference) {
		// generally we do not want to render the qualifier
		appendSql( columnReference.getColumnExpression() );
	}
}
