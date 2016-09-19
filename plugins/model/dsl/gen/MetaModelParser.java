// Generated from /Users/ludovicmouline/Documents/Thesis/MwDB/plugins/model/dsl/src/main/antlr4/MetaModel.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MetaModelParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, STRING=22, IDENT=23, TYPE_NAME=24, 
		NUMBER=25, WS=26, SL_COMMENT=27;
	public static final int
		RULE_metamodel = 0, RULE_indexDeclr = 1, RULE_indexLiterals = 2, RULE_enumDeclr = 3, 
		RULE_enumLiterals = 4, RULE_classDeclr = 5, RULE_parentsDeclr = 6, RULE_semanticDeclr = 7, 
		RULE_semanticWith = 8, RULE_semanticUsing = 9, RULE_semanticFrom = 10, 
		RULE_annotation = 11, RULE_attributeType = 12, RULE_attributeDeclaration = 13, 
		RULE_relationDeclaration = 14;
	public static final String[] ruleNames = {
		"metamodel", "indexDeclr", "indexLiterals", "enumDeclr", "enumLiterals", 
		"classDeclr", "parentsDeclr", "semanticDeclr", "semanticWith", "semanticUsing", 
		"semanticFrom", "annotation", "attributeType", "attributeDeclaration", 
		"relationDeclaration"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'index'", "':'", "'{'", "'}'", "','", "'enum'", "'class'", "'extends'", 
		"'with'", "'using'", "'from'", "'learned'", "'derived'", "'global'", "'String'", 
		"'Double'", "'Long'", "'Integer'", "'Boolean'", "'att'", "'rel'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "STRING", 
		"IDENT", "TYPE_NAME", "NUMBER", "WS", "SL_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "MetaModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MetaModelParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class MetamodelContext extends ParserRuleContext {
		public List<EnumDeclrContext> enumDeclr() {
			return getRuleContexts(EnumDeclrContext.class);
		}
		public EnumDeclrContext enumDeclr(int i) {
			return getRuleContext(EnumDeclrContext.class,i);
		}
		public List<ClassDeclrContext> classDeclr() {
			return getRuleContexts(ClassDeclrContext.class);
		}
		public ClassDeclrContext classDeclr(int i) {
			return getRuleContext(ClassDeclrContext.class,i);
		}
		public List<IndexDeclrContext> indexDeclr() {
			return getRuleContexts(IndexDeclrContext.class);
		}
		public IndexDeclrContext indexDeclr(int i) {
			return getRuleContext(IndexDeclrContext.class,i);
		}
		public MetamodelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metamodel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterMetamodel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitMetamodel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitMetamodel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MetamodelContext metamodel() throws RecognitionException {
		MetamodelContext _localctx = new MetamodelContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_metamodel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__5) | (1L << T__6))) != 0)) {
				{
				setState(33);
				switch (_input.LA(1)) {
				case T__5:
					{
					setState(30);
					enumDeclr();
					}
					break;
				case T__6:
					{
					setState(31);
					classDeclr();
					}
					break;
				case T__0:
					{
					setState(32);
					indexDeclr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(37);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IndexDeclrContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(MetaModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(MetaModelParser.IDENT, i);
		}
		public IndexLiteralsContext indexLiterals() {
			return getRuleContext(IndexLiteralsContext.class,0);
		}
		public TerminalNode TYPE_NAME() { return getToken(MetaModelParser.TYPE_NAME, 0); }
		public IndexDeclrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexDeclr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterIndexDeclr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitIndexDeclr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitIndexDeclr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexDeclrContext indexDeclr() throws RecognitionException {
		IndexDeclrContext _localctx = new IndexDeclrContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_indexDeclr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(T__0);
			setState(39);
			match(IDENT);
			setState(40);
			match(T__1);
			setState(41);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==TYPE_NAME) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(42);
			match(T__2);
			setState(43);
			indexLiterals();
			setState(44);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IndexLiteralsContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(MetaModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(MetaModelParser.IDENT, i);
		}
		public IndexLiteralsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexLiterals; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterIndexLiterals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitIndexLiterals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitIndexLiterals(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexLiteralsContext indexLiterals() throws RecognitionException {
		IndexLiteralsContext _localctx = new IndexLiteralsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_indexLiterals);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(IDENT);
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(47);
				match(T__4);
				setState(48);
				match(IDENT);
				}
				}
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnumDeclrContext extends ParserRuleContext {
		public EnumLiteralsContext enumLiterals() {
			return getRuleContext(EnumLiteralsContext.class,0);
		}
		public TerminalNode TYPE_NAME() { return getToken(MetaModelParser.TYPE_NAME, 0); }
		public TerminalNode IDENT() { return getToken(MetaModelParser.IDENT, 0); }
		public EnumDeclrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumDeclr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterEnumDeclr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitEnumDeclr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitEnumDeclr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumDeclrContext enumDeclr() throws RecognitionException {
		EnumDeclrContext _localctx = new EnumDeclrContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_enumDeclr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(T__5);
			setState(55);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==TYPE_NAME) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(56);
			match(T__2);
			setState(57);
			enumLiterals();
			setState(58);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnumLiteralsContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(MetaModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(MetaModelParser.IDENT, i);
		}
		public EnumLiteralsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumLiterals; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterEnumLiterals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitEnumLiterals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitEnumLiterals(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumLiteralsContext enumLiterals() throws RecognitionException {
		EnumLiteralsContext _localctx = new EnumLiteralsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_enumLiterals);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(IDENT);
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(61);
				match(T__4);
				setState(62);
				match(IDENT);
				}
				}
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDeclrContext extends ParserRuleContext {
		public TerminalNode TYPE_NAME() { return getToken(MetaModelParser.TYPE_NAME, 0); }
		public TerminalNode IDENT() { return getToken(MetaModelParser.IDENT, 0); }
		public ParentsDeclrContext parentsDeclr() {
			return getRuleContext(ParentsDeclrContext.class,0);
		}
		public List<AttributeDeclarationContext> attributeDeclaration() {
			return getRuleContexts(AttributeDeclarationContext.class);
		}
		public AttributeDeclarationContext attributeDeclaration(int i) {
			return getRuleContext(AttributeDeclarationContext.class,i);
		}
		public List<RelationDeclarationContext> relationDeclaration() {
			return getRuleContexts(RelationDeclarationContext.class);
		}
		public RelationDeclarationContext relationDeclaration(int i) {
			return getRuleContext(RelationDeclarationContext.class,i);
		}
		public ClassDeclrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDeclr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterClassDeclr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitClassDeclr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitClassDeclr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDeclrContext classDeclr() throws RecognitionException {
		ClassDeclrContext _localctx = new ClassDeclrContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_classDeclr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(T__6);
			setState(69);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==TYPE_NAME) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(71);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(70);
				parentsDeclr();
				}
			}

			setState(73);
			match(T__2);
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__19) | (1L << T__20))) != 0)) {
				{
				setState(76);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(74);
					attributeDeclaration();
					}
					break;
				case 2:
					{
					setState(75);
					relationDeclaration();
					}
					break;
				}
				}
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParentsDeclrContext extends ParserRuleContext {
		public TerminalNode TYPE_NAME() { return getToken(MetaModelParser.TYPE_NAME, 0); }
		public TerminalNode IDENT() { return getToken(MetaModelParser.IDENT, 0); }
		public ParentsDeclrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parentsDeclr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterParentsDeclr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitParentsDeclr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitParentsDeclr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParentsDeclrContext parentsDeclr() throws RecognitionException {
		ParentsDeclrContext _localctx = new ParentsDeclrContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_parentsDeclr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(T__7);
			setState(84);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==TYPE_NAME) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SemanticDeclrContext extends ParserRuleContext {
		public List<SemanticUsingContext> semanticUsing() {
			return getRuleContexts(SemanticUsingContext.class);
		}
		public SemanticUsingContext semanticUsing(int i) {
			return getRuleContext(SemanticUsingContext.class,i);
		}
		public List<SemanticFromContext> semanticFrom() {
			return getRuleContexts(SemanticFromContext.class);
		}
		public SemanticFromContext semanticFrom(int i) {
			return getRuleContext(SemanticFromContext.class,i);
		}
		public List<SemanticWithContext> semanticWith() {
			return getRuleContexts(SemanticWithContext.class);
		}
		public SemanticWithContext semanticWith(int i) {
			return getRuleContext(SemanticWithContext.class,i);
		}
		public SemanticDeclrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_semanticDeclr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterSemanticDeclr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitSemanticDeclr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitSemanticDeclr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SemanticDeclrContext semanticDeclr() throws RecognitionException {
		SemanticDeclrContext _localctx = new SemanticDeclrContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_semanticDeclr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			match(T__2);
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__10))) != 0)) {
				{
				setState(90);
				switch (_input.LA(1)) {
				case T__9:
					{
					setState(87);
					semanticUsing();
					}
					break;
				case T__10:
					{
					setState(88);
					semanticFrom();
					}
					break;
				case T__8:
					{
					setState(89);
					semanticWith();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(95);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SemanticWithContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(MetaModelParser.IDENT, 0); }
		public TerminalNode STRING() { return getToken(MetaModelParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(MetaModelParser.NUMBER, 0); }
		public SemanticWithContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_semanticWith; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterSemanticWith(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitSemanticWith(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitSemanticWith(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SemanticWithContext semanticWith() throws RecognitionException {
		SemanticWithContext _localctx = new SemanticWithContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_semanticWith);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(T__8);
			setState(98);
			match(IDENT);
			setState(99);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==NUMBER) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SemanticUsingContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(MetaModelParser.STRING, 0); }
		public SemanticUsingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_semanticUsing; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterSemanticUsing(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitSemanticUsing(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitSemanticUsing(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SemanticUsingContext semanticUsing() throws RecognitionException {
		SemanticUsingContext _localctx = new SemanticUsingContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_semanticUsing);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__9);
			setState(102);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SemanticFromContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(MetaModelParser.STRING, 0); }
		public SemanticFromContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_semanticFrom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterSemanticFrom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitSemanticFrom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitSemanticFrom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SemanticFromContext semanticFrom() throws RecognitionException {
		SemanticFromContext _localctx = new SemanticFromContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_semanticFrom);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(T__10);
			setState(105);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnnotationContext extends ParserRuleContext {
		public AnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitAnnotation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitAnnotation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnnotationContext annotation() throws RecognitionException {
		AnnotationContext _localctx = new AnnotationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_annotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeTypeContext extends ParserRuleContext {
		public TerminalNode TYPE_NAME() { return getToken(MetaModelParser.TYPE_NAME, 0); }
		public AttributeTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterAttributeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitAttributeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitAttributeType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeTypeContext attributeType() throws RecognitionException {
		AttributeTypeContext _localctx = new AttributeTypeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_attributeType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << TYPE_NAME))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(MetaModelParser.IDENT, 0); }
		public AttributeTypeContext attributeType() {
			return getRuleContext(AttributeTypeContext.class,0);
		}
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public SemanticDeclrContext semanticDeclr() {
			return getRuleContext(SemanticDeclrContext.class,0);
		}
		public AttributeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterAttributeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitAttributeDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitAttributeDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeDeclarationContext attributeDeclaration() throws RecognitionException {
		AttributeDeclarationContext _localctx = new AttributeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_attributeDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) {
				{
				{
				setState(111);
				annotation();
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(117);
			match(T__19);
			setState(118);
			match(IDENT);
			setState(119);
			match(T__1);
			setState(120);
			attributeType();
			setState(122);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(121);
				semanticDeclr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationDeclarationContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(MetaModelParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(MetaModelParser.IDENT, i);
		}
		public TerminalNode TYPE_NAME() { return getToken(MetaModelParser.TYPE_NAME, 0); }
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public SemanticDeclrContext semanticDeclr() {
			return getRuleContext(SemanticDeclrContext.class,0);
		}
		public RelationDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).enterRelationDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MetaModelListener ) ((MetaModelListener)listener).exitRelationDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MetaModelVisitor ) return ((MetaModelVisitor<? extends T>)visitor).visitRelationDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationDeclarationContext relationDeclaration() throws RecognitionException {
		RelationDeclarationContext _localctx = new RelationDeclarationContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_relationDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) {
				{
				{
				setState(124);
				annotation();
				}
				}
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(130);
			match(T__20);
			setState(131);
			match(IDENT);
			setState(132);
			match(T__1);
			setState(133);
			_la = _input.LA(1);
			if ( !(_la==IDENT || _la==TYPE_NAME) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(135);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(134);
				semanticDeclr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\35\u008c\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\7\2$\n"+
		"\2\f\2\16\2\'\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4\64"+
		"\n\4\f\4\16\4\67\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\7\6B\n\6\f\6"+
		"\16\6E\13\6\3\7\3\7\3\7\5\7J\n\7\3\7\3\7\3\7\7\7O\n\7\f\7\16\7R\13\7\3"+
		"\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\7\t]\n\t\f\t\16\t`\13\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\17\7\17s\n"+
		"\17\f\17\16\17v\13\17\3\17\3\17\3\17\3\17\3\17\5\17}\n\17\3\20\7\20\u0080"+
		"\n\20\f\20\16\20\u0083\13\20\3\20\3\20\3\20\3\20\3\20\5\20\u008a\n\20"+
		"\3\20\2\2\21\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36\2\6\3\2\31\32\4\2"+
		"\30\30\33\33\3\2\16\20\4\2\21\25\32\32\u008b\2%\3\2\2\2\4(\3\2\2\2\6\60"+
		"\3\2\2\2\b8\3\2\2\2\n>\3\2\2\2\fF\3\2\2\2\16U\3\2\2\2\20X\3\2\2\2\22c"+
		"\3\2\2\2\24g\3\2\2\2\26j\3\2\2\2\30m\3\2\2\2\32o\3\2\2\2\34t\3\2\2\2\36"+
		"\u0081\3\2\2\2 $\5\b\5\2!$\5\f\7\2\"$\5\4\3\2# \3\2\2\2#!\3\2\2\2#\"\3"+
		"\2\2\2$\'\3\2\2\2%#\3\2\2\2%&\3\2\2\2&\3\3\2\2\2\'%\3\2\2\2()\7\3\2\2"+
		")*\7\31\2\2*+\7\4\2\2+,\t\2\2\2,-\7\5\2\2-.\5\6\4\2./\7\6\2\2/\5\3\2\2"+
		"\2\60\65\7\31\2\2\61\62\7\7\2\2\62\64\7\31\2\2\63\61\3\2\2\2\64\67\3\2"+
		"\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66\7\3\2\2\2\67\65\3\2\2\289\7\b\2\2"+
		"9:\t\2\2\2:;\7\5\2\2;<\5\n\6\2<=\7\6\2\2=\t\3\2\2\2>C\7\31\2\2?@\7\7\2"+
		"\2@B\7\31\2\2A?\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2D\13\3\2\2\2EC\3"+
		"\2\2\2FG\7\t\2\2GI\t\2\2\2HJ\5\16\b\2IH\3\2\2\2IJ\3\2\2\2JK\3\2\2\2KP"+
		"\7\5\2\2LO\5\34\17\2MO\5\36\20\2NL\3\2\2\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2"+
		"\2PQ\3\2\2\2QS\3\2\2\2RP\3\2\2\2ST\7\6\2\2T\r\3\2\2\2UV\7\n\2\2VW\t\2"+
		"\2\2W\17\3\2\2\2X^\7\5\2\2Y]\5\24\13\2Z]\5\26\f\2[]\5\22\n\2\\Y\3\2\2"+
		"\2\\Z\3\2\2\2\\[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_a\3\2\2\2`^\3"+
		"\2\2\2ab\7\6\2\2b\21\3\2\2\2cd\7\13\2\2de\7\31\2\2ef\t\3\2\2f\23\3\2\2"+
		"\2gh\7\f\2\2hi\7\30\2\2i\25\3\2\2\2jk\7\r\2\2kl\7\30\2\2l\27\3\2\2\2m"+
		"n\t\4\2\2n\31\3\2\2\2op\t\5\2\2p\33\3\2\2\2qs\5\30\r\2rq\3\2\2\2sv\3\2"+
		"\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2\2\2vt\3\2\2\2wx\7\26\2\2xy\7\31\2\2yz\7"+
		"\4\2\2z|\5\32\16\2{}\5\20\t\2|{\3\2\2\2|}\3\2\2\2}\35\3\2\2\2~\u0080\5"+
		"\30\r\2\177~\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082"+
		"\3\2\2\2\u0082\u0084\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0085\7\27\2\2"+
		"\u0085\u0086\7\31\2\2\u0086\u0087\7\4\2\2\u0087\u0089\t\2\2\2\u0088\u008a"+
		"\5\20\t\2\u0089\u0088\3\2\2\2\u0089\u008a\3\2\2\2\u008a\37\3\2\2\2\17"+
		"#%\65CINP\\^t|\u0081\u0089";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}