# eXtendGql Traversal 

The package provides the ability to traverse on **GraphQL** document.
The package uses an observable design pattern, which will give you the ability to control the traversing and to 
access  **GraphQL** nodes.

The traversal module traverse the entire **GraphQL** query or mutation.
Provide for each visited element context with:
- Current **GraphQL** node
- Parent **GraphQL**  Node
- Level - the depth
- Stack with all the precedence elements in the path of the current browsed entity.
- Type - [DocumentElementType](../com/intuit/library/eXtendGql/DocumentElementType.java)

*TuneableSearchData*

This class help to optimize the traversing over the  **GraphQL** , so in case the application does not 
intend to browse on specific element type e.g. directive, so the traversal will ignore these element types.

# How to - Code Snippets

**main**
```java

GqlTraversal traversal = new GqlTraversal();

StringBuilderObserver gqlStringBuilderObserver = new StringBuilderObserver();

traversal.getGqlTraversalObservable().addObserver(gqlStringBuilderObserver);
traversal.traverse(file);

return gqlStringBuilderObserver.getGqlBrowsedString();
```
**gqlBrowserObserver**
```java
public class StringBuilderObserver implements TraversalObserver {
    private final List<StringBuilderElem> stringBuilderElems = new ArrayList<>();

    private final boolean isIgnoreCollection = true;
    @Override
    public void updateNodeEntry(Node node, Node parentNode, Context context, ObserverAction observerAction) {

        String  message = "";
        DocumentElementType documentElementType = context.getDocumentElementType();
        switch (documentElementType) {

            case DOCUMENT:
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Document", documentElementType.name());

                break;

            case DIRECTIVE:
                message = MessageFormat.format("Name : {0} ||  Type : {1}", ((Directive) node).getName(), documentElementType.name());
                break;
            case FIELD:
                Field field = (Field) node;
                message = MessageFormat.format("Name : {0} || Alias : {1} ||  Type : {2}",
                        field.getName(),
                        field.getAlias(),
                        documentElementType.name());
                break;
            case OPERATION_DEFINITION:
                message = MessageFormat.format("Name : {0} ||  Type : {1}",
                        ((OperationDefinition) node).getOperation().toString(), documentElementType.name());
                break;
            case INLINE_FRAGMENT:
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "InlineFragment",
                        documentElementType.name());

                break;
            case FRAGMENT_DEFINITION:
                message = MessageFormat.format("Name : {0} ||  Type : {1}",
                        ((FragmentDefinition) node).getName(), documentElementType.name());

                break;
            case FRAGMENT_SPREAD:
                message = MessageFormat.format("Node : {0} ||  Type : {1}", ((FragmentSpread) node).getName(), documentElementType.name());
                break;
            case VARIABLE_DEFINITION:
                message = MessageFormat.format("Name : {0} || Default Value : {1} ||  Type : {2}",
                        ((VariableDefinition) node).getName(), ((VariableDefinition) node).getDefaultValue(), documentElementType.name());

                break;
            case ARGUMENT:
                message = MessageFormat.format("Name : {0} || Value : {1} ||  Type : {2}",
                        ((Argument) node).getName(),
                        ((Argument) node).getValue(),
                        documentElementType.name());
                break;
            case ARGUMENTS:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Arguments", documentElementType.name());
                break;
            case SELECTION_SET:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "SelectionSet", documentElementType.name());
                break;
            case VARIABLE_DEFINITIONS:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "VariableDefinitions", documentElementType.name());
                break;
            case DIRECTIVES:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Directives", documentElementType.name());
                break;
            case DEFINITIONS:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Definitions", documentElementType.name());

                break;
        }

        if(Strings.isNullOrEmpty(message)){
            return;
        }

        stringBuilderElems.add(new StringBuilderElem(message, context.getLevel()));

        levels.add(context.getLevel());
        //spaces++;
    }
    private final List<Integer> levels = new ArrayList<>();

    public String getGqlBrowsedPrintedString() {
        return getStringAs(true);
    }

    private String getStringAs(boolean isIdent) {
        int j=0;
        StringBuilder stringBuilder = new StringBuilder();
        for (StringBuilderElem stringBuilderElem : stringBuilderElems) {
            j++;
            String spaceStr = "";
            if( isIdent) {
                for (int i = 0; i < stringBuilderElem.getDepth(); i++) {
                    spaceStr += " ";
                }
                stringBuilder.append(spaceStr + stringBuilderElem.getName() + "\n");
            }else{
                stringBuilder.append( stringBuilderElem.getName() + (j+1<stringBuilderElems.size()? " " : "") );
            }
        }
        return stringBuilder.toString();
    }

    public String getGqlBrowsedString(){
        return getStringAs(false);
    }

    //    int spaces = 0;
    @Override
    public void updateNodeExit( Node node,Node parentNode, Context context, ObserverAction observerAction) {
    }
}
```

## UML diagrams, use of mermaid

```mermaid
sequenceDiagram
GqlTraversal ->> GqlTraversalObservable: init
GqlTraversal ->> Document: parse
GqlTraversal ->> GqlTraversal: browse

GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseOperationDefinition
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseFragmentDefinition
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseDirectives
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseVariableDefinitions
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseArguments
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseInlineFragment
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseFragmentSpread
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeEntry
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeEntry
GqlTraversal ->> GqlTraversal: browseField
GqlTraversal ->> GqlTraversalObservable: notifyObserversNodeExit
    GqlTraversalObservable ->> traversalObservers: notifyObserversNodeExit


```
