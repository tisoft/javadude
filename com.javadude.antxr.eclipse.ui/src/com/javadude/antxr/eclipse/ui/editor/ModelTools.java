/*******************************************************************************
 * Copyright (c) 2008 Scott Stanchfield, based on ANTLR-Eclipse plugin
 *   by Torsten Juergeleit.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors
 *    Torsten Juergeleit - original ANTLR Eclipse plugin
 *    Scott Stanchfield - modifications for ANTXR
 *******************************************************************************/
package com.javadude.antxr.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.List;

import com.javadude.antxr.eclipse.core.parser.ISegment;
import com.javadude.antxr.eclipse.core.parser.ISegmentVisitor;
import com.javadude.antxr.eclipse.core.parser.Rule;

/**
 * Tools for working with a model
 */
public class ModelTools {
    private AntxrEditor fEditor;

    /**
     * Create an instance of ModelTools
     * @param anEditor the editor to work against
     */
    public ModelTools(AntxrEditor anEditor) {
        fEditor = anEditor;
    }

    /**
     * Uses visitor design pattern to find segment which contains given line.
     * @param aLine  line to find according segment for
     * @return segment containing given line or null if no segment found
     */
    public ISegment getSegment(int aLine) {
        SegmentLineVisitor visitor = new SegmentLineVisitor(aLine);
        fEditor.getReconcilingStrategy().getRootSegment().accept(visitor);
        return visitor.getSegment();
    }

    /**
     * Uses visitor design pattern to find segment with given name.
     * @param aName  name to find according segment for
     * @return segment with given name or null if no segment found
     */
    public ISegment getSegment(String aName) {
        SegmentRuleNameVisitor visitor = new SegmentRuleNameVisitor(aName);
        fEditor.getReconcilingStrategy().getRootSegment().accept(visitor);
        return visitor.getSegment();
    }

    /**
     * Uses visitor design pattern to find all rules with given prefix.
     * @param aPrefix the prefix
     * @return the rules with that given prefix
     */
    public String[] getRules(String aPrefix) {
        SegmentRulesVisitor visitor = new SegmentRulesVisitor(aPrefix);
        fEditor.getReconcilingStrategy().getRootSegment().accept(visitor);
        return visitor.getRules();
    }

    private class SegmentLineVisitor implements ISegmentVisitor {
        private int fLine;
        private ISegment fSegment;

        /**
         * Create an instance
         * @param aLine the line to visit
         */
        public SegmentLineVisitor(int aLine) {
            fLine = aLine;
            fSegment = null;
        }

        public boolean visit(ISegment aSegment) {
            boolean more;
            if (fLine >= aSegment.getStartLine() &&
                                             fLine <= aSegment.getEndLine()) {
                fSegment = aSegment;
                more = false;
            } else {
                more = true;
            }
            return more;
        }

        /**
         * @return the segment
         */
        public ISegment getSegment() {
            return fSegment;
        }
    }

    private class SegmentRulesVisitor implements ISegmentVisitor {
        private String fPrefix;
        private List<String> fRules;

        /**
         * Create an instance
         * @param aPrefix the prefix
         */
        public SegmentRulesVisitor(String aPrefix) {
            fPrefix = aPrefix;
            fRules = new ArrayList<String>();
        }

        public boolean visit(ISegment aSegment) {
            if (aSegment instanceof Rule) {
                String name = ((Rule)aSegment).getName();
                if (name.startsWith(fPrefix)) {
                    fRules.add(name);
                }
            }
            return true;
        }

        /**
         * Get all the rules
         * @return the array of rules
         */
        public String[] getRules() {
            return fRules.toArray(new String[fRules.size()]);
        }
    }

    private class SegmentRuleNameVisitor implements ISegmentVisitor {
        private String fName;
        private ISegment fSegment;

        /**
         * Create an instance
         * @param aName the name of the rule
         */
        public SegmentRuleNameVisitor(String aName) {
            fName = aName;
            fSegment = null;
        }

        public boolean visit(ISegment aSegment) {
            boolean more;
            if (aSegment instanceof Rule &&
                                    ((Rule)aSegment).getName().equals(fName)) {
                fSegment = aSegment;
                more = false;
            } else {
                more = true;
            }
            return more;
        }

        /**
         * @return the segment
         */
        public ISegment getSegment() {
            return fSegment;
        }
    }
}
