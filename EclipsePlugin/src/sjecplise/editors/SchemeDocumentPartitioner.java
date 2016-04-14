package sjecplise.editors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitionerExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;

import se.pp.forsberg.scheme.DebugInformation;
import se.pp.forsberg.scheme.DebugInformation.Atmosphere;
import se.pp.forsberg.scheme.DebugInformation.AtmosphereType;
import se.pp.forsberg.scheme.DebugInformation.Node;
import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Eof;
import se.pp.forsberg.scheme.values.Identifier;
import se.pp.forsberg.scheme.values.String;
import se.pp.forsberg.scheme.values.Character;
import se.pp.forsberg.scheme.values.Value;
import se.pp.forsberg.scheme.values.numbers.Number;

public class SchemeDocumentPartitioner implements IDocumentPartitioner, IDocumentPartitionerExtension,
    IDocumentPartitionerExtension2, IDocumentPartitionerExtension3 {
  
  enum Type {
    IDENTIFIER(Identifier.class, true),
    STRING(String.class, true),
    CHARACTER(Character.class, true),
    BOOLEAN(Boolean.class, true),
    NUMBER(Number.class, true),
    BEGIN_LIST(true),
    BEGIN_VECTOR(true),
    BEGIN_BYTEVECTOR(true),
    END_LIST(true),
    COMMENT(true),
    DIRECTIVE(true),
    WHITESPACE(false);
    
    private Class<?> clazz;
    private boolean delimited;
    
    private Type(boolean delimited) { this(null, delimited); }
    private Type(Class<?> clazz, boolean delimited) {
      this.clazz = clazz;
      this.delimited = delimited;
    }
    public Class<?> getValueClass() { return clazz; }
    public boolean isDelimited() { return delimited; }
    public boolean isOpen() { return !delimited; }
  }
  
  class TypedRegion implements ITypedRegion {

    private Type type;
    private int offset, length;
    
    public TypedRegion(Type type, int offset, int length) {
      this.type = type;
      this.offset = offset;
      this.length = length;
    }
    
    @Override public int getLength() { return length; }
    @Override public int getOffset() { return offset; }
    @Override public java.lang.String getType() { return type.name(); }
    public Type getTypeEnum() { return type; }
    public boolean isDelimited() { return type.isDelimited(); }
    public boolean isOpen() { return type.isOpen(); }

    public boolean overlaps(int offset, int length) {
      return this.offset < offset + length ||
             offset < this.offset + this.length;
    }
    
  }
  
  IDocument document;
  DocumentRewriteSession rewriteSession;
  Parser parser;
  DebugInformation debugInformation;
  static final java.lang.String[] contentTypes;
  static final Map<Class<?>, Type> classToTypeMap;
  static {
    classToTypeMap = new HashMap<Class<?>, Type>();
    List<java.lang.String> contentTypeList = new ArrayList<java.lang.String>();
    for (Type type: Type.values()) {
      if (type.getClass() != null) {
        classToTypeMap.put(type.getClass(), type);
      }
      contentTypeList.add(type.name());
    }
    contentTypes = new java.lang.String[contentTypeList.size()];
    contentTypeList.toArray(contentTypes);
  }
  TypedRegion[] regions;
  
  // IDocumentPartitionerExtension3
  @Override
  public void startRewriteSession(DocumentRewriteSession session) throws IllegalStateException {
    rewriteSession = session;
  }

  @Override
  public void stopRewriteSession(DocumentRewriteSession session) {
    handleRewrite(null);
    rewriteSession = null;
  }

  @Override
  public DocumentRewriteSession getActiveRewriteSession() {
    return rewriteSession;
  }

  @Override
  public void connect(IDocument document, boolean delayInitialization) {
    this.document = document;
    if (!delayInitialization) {
      repartition(null);
    }
  }
  
  // IDocumentPartitionerExtension2
  @Override
  public java.lang.String[] getManagingPositionCategories() {
    return null; // What is this?
  }

  @Override
  public java.lang.String getContentType(int offset, boolean preferOpenPartitions) {
    if (regions == null) {
      repartition(null);
    }
    return getContentType(offset, preferOpenPartitions, 0, regions.length-1);
  }

  @Override
  public ITypedRegion getPartition(int offset, boolean preferOpenPartitions) {
    return getPartition(offset, preferOpenPartitions, 0, regions.length-1);
  }

  @Override
  public ITypedRegion[] computePartitioning(int offset, int length, boolean includeZeroLengthPartitions) {
    List<ITypedRegion> result = new ArrayList<ITypedRegion>();
    int i = getPartitionOffset(offset, true);
    TypedRegion region = regions[i];
    if (i > 0 && region.getOffset() == offset) {
      i--;
      region = regions[i];
    }
    while (region.overlaps(offset, length)) {
      if (!includeZeroLengthPartitions && region.getLength() == 0) {
        continue;
      }
      result.add(region);
    }
    ITypedRegion[] array = new ITypedRegion[result.size()];
    return result.toArray(array);
  }

  @Override
  public IRegion documentChanged2(DocumentEvent event) {
    // TODO Better
    repartition(event);
    TypedRegion last = regions[regions.length-1];
    return new Region(0, last.offset + last.length);
  }

  // IDocumentPartitioner
  @Override
  public void connect(IDocument document) {
    connect(document, false);
  }

  @Override
  public void disconnect() {
    // ??
  }

  @Override
  public void documentAboutToBeChanged(DocumentEvent event) {
  }

  @Override
  public boolean documentChanged(DocumentEvent event) {
    IRegion changedRegion = documentChanged2(event);
    return changedRegion != null;
  }

  @Override
  public java.lang.String[] getLegalContentTypes() {
    return contentTypes;
  }

  @Override
  public java.lang.String getContentType(int offset) {
    return getContentType(offset, false);
  }

  @Override
  public ITypedRegion[] computePartitioning(int offset, int length) {
    return computePartitioning(offset, length, false);

  }

  @Override
  public ITypedRegion getPartition(int offset) {
    return getPartition(offset, false);
  }

  // Private partitioning 
  
  private void handleRewrite(DocumentEvent event) {
    repartition(event);
  }

  private java.lang.String getContentType(int offset, boolean preferOpenPartitions, int start, int stop) {
    return getPartition(offset, preferOpenPartitions, start, stop).getType();
  }
  private TypedRegion getPartition(int offset, boolean preferOpenPartitions, int start, int stop) {
    return regions[getPartitionOffset(offset, preferOpenPartitions, start, stop)];
  }
  private int getPartitionOffset(int offset, boolean preferOpenPartitions) {
    return getPartitionOffset(offset, preferOpenPartitions, 0, regions.length-1);
  }
  private int getPartitionOffset(int offset, boolean preferOpenPartitions, int start, int stop) {
    int mid = (start+stop)/2;
    TypedRegion region = regions[mid];
    if (offset < region.offset) {
      return getPartitionOffset(offset, preferOpenPartitions, start, mid);
    }
    if (offset > region.getLength() + region.getLength()) {
      return getPartitionOffset(offset, preferOpenPartitions, mid, stop);
    }
    if (region.getOffset() == offset) {
      if (mid == 0) return mid;
      TypedRegion before = regions[mid-1];
      if (preferOpenPartitions) {
        if (before.isOpen()) {
          return mid-1;
        }
        return mid;
      }
    } else if (region.getOffset() + region.getLength() == offset) {
      if (mid == regions.length-1) return mid;
      if (preferOpenPartitions) {
        if (region.isOpen()) {
          return mid;
        }
        return mid+1;
      }
    }
    return mid;
  }
  
  private void repartition(DocumentEvent event) {
    if (rewriteSession != null) return;
    parse();
    DebugInformation.Node root = debugInformation.getRoot();
    List<DebugInformation.Atmosphere> atmosphere = debugInformation.getAtmosphere();
    List<TypedRegion> regions = new ArrayList<TypedRegion>();
    
    PartitionState state = new PartitionState();
    repartition(state, root, atmosphere, regions);
    if (state.offset != document.getLength()) {
      regions.add(new TypedRegion(Type.WHITESPACE, state.offset, document.getLength() - state.offset));
      state.lastWhitespace = true;
    }
    if (!state.lastWhitespace) {
      regions.add(new TypedRegion(Type.WHITESPACE, document.getLength(), 0));
    }
    //fireDocumentChanged(event);
  }
  private class PartitionState {
    public int offset, atmosphereIndex;
    public boolean lastWhitespace;
  }
  private void repartition(PartitionState state, Node node, List<Atmosphere> atmosphereList, List<TypedRegion> regions) {
    Atmosphere atmosphere = null;
    int start = Integer.MAX_VALUE;
    if (state.atmosphereIndex < atmosphereList.size()) {
      start = atmosphereList.get(state.atmosphereIndex).getOffset();
    }
    if (node != null && node.getOffset() < start) {
      start = node.getOffset();
    }
    if (start > state.offset) {
      regions.add(new TypedRegion(Type.WHITESPACE, state.offset, start - state.offset));
      state.offset = start;
      state.lastWhitespace = true;
    }
    if (state.atmosphereIndex < atmosphereList.size()) {
      atmosphere = atmosphereList.get(state.atmosphereIndex);
      if (atmosphere.getOffset() < state.offset) {
        // Bad programmer....
        state.atmosphereIndex++;
        repartition(state, node, atmosphereList, regions);
        return;
      }
      if (atmosphere.getOffset() == state.offset) {
        if (!state.lastWhitespace && atmosphere.getType() != AtmosphereType.WHITESPACE) {
          regions.add(new TypedRegion(Type.WHITESPACE, state.offset, 0));
          state.lastWhitespace = true;
        }
        Type type = null;
        switch (atmosphere.getType()) {
        case COMMENT: type = Type.COMMENT; break;
        case DIRECTIVE: type = Type.DIRECTIVE; break;
        case WHITESPACE: type = Type.WHITESPACE; break;
        }
        regions.add(new TypedRegion(type, state.offset, atmosphere.getLength()));
        state.offset += atmosphere.getLength();
        state.lastWhitespace = atmosphere.getType() == AtmosphereType.WHITESPACE;
        repartition(state, node, atmosphereList, regions);
        return;
      }
      start = atmosphere.getOffset();
    }
    if (node != null ) {
      if (node.getOffset() < state.offset) {
        // Bad programmer...
        List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
          repartition(state, children.get(i), atmosphereList, regions);
        }
        return;
      }
      if (node.getOffset() == state.offset) {
        Value value = node.getValue();
        if (value.isPair()) { // (
          regions.add(new TypedRegion(Type.BEGIN_LIST, state.offset, 1));
          state.offset++;
          state.lastWhitespace = false;
        } else if (value.isVector()) { // #(
          regions.add(new TypedRegion(Type.BEGIN_VECTOR, state.offset, 2));
          state.offset += 2;
          state.lastWhitespace = false;
        } else if (value.isVector()) { // #u8(
          regions.add(new TypedRegion(Type.BEGIN_BYTEVECTOR, state.offset, 4));
          state.offset += 4;
          state.lastWhitespace = false;
        } else if (value.isNull()) {
          return;
        } else { 
          regions.add(new TypedRegion(getType(value.getClass()), node.getOffset(), node.getLength()));
          state.offset += node.getLength();
          state.lastWhitespace = false;
          return;
        }
        // List, vector, bytevector
        List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
          repartition(state, children.get(i), atmosphereList, regions);
        }
        int end = node.getOffset() + node.getLength();
        if (state.offset < end - 1) {
          regions.add(new TypedRegion(Type.WHITESPACE, state.offset, end - state.offset - 1));
          state.offset = end - 1;
          state.lastWhitespace = true;
        }
        if (!state.lastWhitespace) {
          regions.add(new TypedRegion(Type.WHITESPACE, state.offset, 0));
        }
        regions.add(new TypedRegion(Type.END_LIST, end - 1, 1));
        state.offset = end;
        state.lastWhitespace = false;
        return;
      }
    }
  }

  private Type getType(Class<?> clazz) {
    while (clazz != null) {
      Type type = classToTypeMap.get(clazz);
      if (type != null) return type;
      clazz = clazz.getSuperclass();
    }
    return null;
  }

  protected void parse() {
    parser = new Parser(new StringReader(document.get()));
    parser.getTokenizer().setEclipseMode(true);
    try {
      while (parser.read() != Eof.EOF);
      debugInformation = parser.getDebugInformation();
    } catch (SchemeException e) {
      // Cannot happen in Eclipse mode
      e.printStackTrace();
    }
  }

  public static java.lang.String[] getContentTypes() {
    return contentTypes;
  }

  // SchemeDocumentModel
//  
//  private List<SchemeDocumentListener> documentDamageListeners = new ArrayList<SchemeDocumentListener>();
//  @Override
//  public void addDocumentListener(SchemeDocumentListener listener) {
//    documentDamageListeners.add(listener);
//  }
//  @Override
//  public void removeDocumentListener(SchemeDocumentListener listener) {
//    documentDamageListeners.remove(listener);
//  }
//  private void fireDocumentChanged(DocumentEvent documentEvent) {
//    SchemeDocumentEvent e = new SchemeDocumentEvent(documentEvent);
//    for (SchemeDocumentListener listener: documentDamageListeners) {
//      listener.documentChanged(e);
//    }
//  }
  
}
