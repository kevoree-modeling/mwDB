// Generated from /Users/ludovicmouline/Documents/Thesis/MwDB/plugins/model/dsl/src/main/antlr4/MetaModel.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MetaModelParser}.
 */
public interface MetaModelListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#metamodel}.
	 * @param ctx the parse tree
	 */
	void enterMetamodel(MetaModelParser.MetamodelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#metamodel}.
	 * @param ctx the parse tree
	 */
	void exitMetamodel(MetaModelParser.MetamodelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#indexDeclr}.
	 * @param ctx the parse tree
	 */
	void enterIndexDeclr(MetaModelParser.IndexDeclrContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#indexDeclr}.
	 * @param ctx the parse tree
	 */
	void exitIndexDeclr(MetaModelParser.IndexDeclrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#indexLiterals}.
	 * @param ctx the parse tree
	 */
	void enterIndexLiterals(MetaModelParser.IndexLiteralsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#indexLiterals}.
	 * @param ctx the parse tree
	 */
	void exitIndexLiterals(MetaModelParser.IndexLiteralsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#enumDeclr}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclr(MetaModelParser.EnumDeclrContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#enumDeclr}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclr(MetaModelParser.EnumDeclrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#enumLiterals}.
	 * @param ctx the parse tree
	 */
	void enterEnumLiterals(MetaModelParser.EnumLiteralsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#enumLiterals}.
	 * @param ctx the parse tree
	 */
	void exitEnumLiterals(MetaModelParser.EnumLiteralsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#classDeclr}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclr(MetaModelParser.ClassDeclrContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#classDeclr}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclr(MetaModelParser.ClassDeclrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#parentsDeclr}.
	 * @param ctx the parse tree
	 */
	void enterParentsDeclr(MetaModelParser.ParentsDeclrContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#parentsDeclr}.
	 * @param ctx the parse tree
	 */
	void exitParentsDeclr(MetaModelParser.ParentsDeclrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#semanticDeclr}.
	 * @param ctx the parse tree
	 */
	void enterSemanticDeclr(MetaModelParser.SemanticDeclrContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#semanticDeclr}.
	 * @param ctx the parse tree
	 */
	void exitSemanticDeclr(MetaModelParser.SemanticDeclrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#semanticWith}.
	 * @param ctx the parse tree
	 */
	void enterSemanticWith(MetaModelParser.SemanticWithContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#semanticWith}.
	 * @param ctx the parse tree
	 */
	void exitSemanticWith(MetaModelParser.SemanticWithContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#semanticUsing}.
	 * @param ctx the parse tree
	 */
	void enterSemanticUsing(MetaModelParser.SemanticUsingContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#semanticUsing}.
	 * @param ctx the parse tree
	 */
	void exitSemanticUsing(MetaModelParser.SemanticUsingContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#semanticFrom}.
	 * @param ctx the parse tree
	 */
	void enterSemanticFrom(MetaModelParser.SemanticFromContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#semanticFrom}.
	 * @param ctx the parse tree
	 */
	void exitSemanticFrom(MetaModelParser.SemanticFromContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(MetaModelParser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(MetaModelParser.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#attributeType}.
	 * @param ctx the parse tree
	 */
	void enterAttributeType(MetaModelParser.AttributeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#attributeType}.
	 * @param ctx the parse tree
	 */
	void exitAttributeType(MetaModelParser.AttributeTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAttributeDeclaration(MetaModelParser.AttributeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAttributeDeclaration(MetaModelParser.AttributeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MetaModelParser#relationDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterRelationDeclaration(MetaModelParser.RelationDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MetaModelParser#relationDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitRelationDeclaration(MetaModelParser.RelationDeclarationContext ctx);
}