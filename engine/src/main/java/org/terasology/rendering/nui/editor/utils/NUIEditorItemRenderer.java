/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.editor.utils;

import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.itemRendering.StringTextIconRenderer;
import org.terasology.rendering.nui.widgets.treeView.JsonTree;
import org.terasology.rendering.nui.widgets.treeView.JsonTreeValue;
import org.terasology.rendering.nui.widgets.treeView.Tree;
import org.terasology.rendering.nui.widgets.treeView.TreeModel;
import org.terasology.utilities.Assets;

import java.util.Optional;

public class NUIEditorItemRenderer extends StringTextIconRenderer<JsonTreeValue> {
    private static final String ARRAY_TEXTURE_NAME = "array";
    private static final String ATTRIBUTE_TEXTURE_NAME = "attribute";
    private static final String ICON_BLANK = "engine:icon_blank";
    private static final String OBJECT_TEXTURE_NAME = "object";

    /**
     * The tree model the nodes of which are to be rendered
     * using this renderer.
     */
    private TreeModel<JsonTreeValue> editorTreeViewModel;

    public NUIEditorItemRenderer(TreeModel<JsonTreeValue> editorTreeViewModel) {
        super(2, 2, 5, 5);
        this.editorTreeViewModel = editorTreeViewModel;
    }

    @Override
    public String getString(JsonTreeValue value) {
        if (value.getType() == JsonTreeValue.Type.OBJECT) {
            JsonTree node = (JsonTree) editorTreeViewModel.getNodeByValue(value);

            // If a node has "type": "..." and/or "id": "..." children, use their values to build the result string.
            if (node != null) {
                String resultString = "";
                for (Tree<JsonTreeValue> child : node.getChildren()) {
                    JsonTreeValue childValue = child.getValue();

                    if (childValue.getType() == JsonTreeValue.Type.KEY_VALUE_PAIR) {
                        if ("type".equals(childValue.getKey())) {
                            resultString += String.format("\"type\": \"%s\"", childValue.getValue());
                        } else if ("id".equals(childValue.getKey())) {
                            resultString += String.format("; \"id\": \"%s\"", childValue.getValue());
                        }
                    }
                }

                if (!resultString.isEmpty()) {
                    return String.format("%s{ %s }", value.getKey() != null ? value.getKey() + " " : "", resultString);
                }
            }
        }

        // Otherwise use JsonTreeValue.toString() output.
        return value.toString();
    }

    @Override
    public Texture getTexture(JsonTreeValue value) {
        String textureName = null;

        if (value.getType() == JsonTreeValue.Type.KEY_VALUE_PAIR) {
            textureName = ATTRIBUTE_TEXTURE_NAME;
        } else if (value.getType() == JsonTreeValue.Type.OBJECT) {
            JsonTree node = (JsonTree) editorTreeViewModel.getNodeByValue(value);

            // If the node has a "type": "..." child, retrieve the texture by the type name.
            if (node != null) {
                // If the node has no type and is a root node, do not draw an icon.
                if (node.isRoot()) {
                    return null;
                } else if (!node.isRoot() && "elements".equals(node.getParent().getValue().getKey())) {
                    textureName = node.getValue().getKey();
                } else {
                    for (Tree<JsonTreeValue> child : node.getChildren()) {
                        JsonTreeValue childValue = child.getValue();
                        if (childValue.getType() == JsonTreeValue.Type.KEY_VALUE_PAIR
                            && "type".equals(childValue.getKey())) {
                            textureName = (String) childValue.getValue();
                        }
                    }
                }
            }

            // Otherwise use the default texture.
            if (textureName == null) {
                textureName = OBJECT_TEXTURE_NAME;
            }
        } else if (value.getType() == JsonTreeValue.Type.ARRAY) {
            textureName = ARRAY_TEXTURE_NAME;
        } else if (value.getKey() != null) {
            textureName = value.getKey();
        } else {
            return null;
        }

        if (textureName != null) {
            Optional<Texture> texture = Assets.getTexture(String.format("engine:editor_%s", textureName));
            if (texture.isPresent()) {
                return texture.get();
            }
        }

        // Use a transparent 16x16 icon for consistent child rendering.
        return Assets.getTexture(ICON_BLANK).get();
    }
}
