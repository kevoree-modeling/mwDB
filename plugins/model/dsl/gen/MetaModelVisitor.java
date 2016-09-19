// Generated from /Users/ludovicmouline/Documents/Thesis/MwDB/plugins/model/dsl/src/main/antlr4/MetaModel.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MetaModelParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MetaModelVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#metamodel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetamodel(MetaModelParser.MetamodelContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#indexDeclr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexDeclr(MetaModelParser.IndexDeclrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#indexLiterals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexLiterals(MetaModelParser.IndexLiteralsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#enumDeclr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumDeclr(MetaModelParser.EnumDeclrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#enumLiterals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumLiterals(MetaModelParser.EnumLiteralsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#classDeclr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclr(MetaModelParser.ClassDeclrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#parentsDeclr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentsDeclr(MetaModelParser.ParentsDeclrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#semanticDeclr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSemanticDeclr(MetaModelParser.SemanticDeclrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#semanticWith}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSemanticWith(MetaModelParser.SemanticWithContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#semanticUsing}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSemanticUsing(MetaModelParser.SemanticUsingContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#semanticFrom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSemanticFrom(MetaModelParser.SemanticFromContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#annotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotation(MetaModelParser.AnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#attributeType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeType(MetaModelParser.AttributeTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeDeclaration(MetaModelParser.AttributeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MetaModelParser#relationDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationDeclaration(MetaModelParser.RelationDeclarationContext ctx);
}