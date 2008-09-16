/*******************************************************************************
 * Copyright (c) 2008 Scott Stanchfield
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.javadude.dependencies.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaModelException;

import com.javadude.annotation.Bean;
import com.javadude.annotation.Property;
import com.javadude.dependencies.Dependency;

@Bean(superclass=Command.class,
      properties={
		@Property(name="dependency", type=Dependency.class),
		@Property(name="deletedEntry", type=IClasspathEntry.class),
		@Property(name="deletedPosition", type=int.class),
})
public class DeleteCommand extends DeleteCommandGen {

    public DeleteCommand(Dependency dependency) {
        setDependency(dependency);
    }

    @Override
    public void execute() {
        // delete the target dep from the project
        try {
            IClasspathEntry[] rawClasspath = getDependency().getSource().getRawClasspath();
            List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();
            for (IClasspathEntry entry : rawClasspath) {
                if (entry.getEntryKind() != IClasspathEntry.CPE_PROJECT ||
                    !entry.getPath().equals(getDependency().getTarget().getPath())) {
                    newClasspath.add(entry);
                } else {
                    setDeletedPosition(newClasspath.size());
                    setDeletedEntry(entry);
                }
            }
            rawClasspath = newClasspath.toArray(new IClasspathEntry[rawClasspath.length-1]);
            getDependency().getSource().setRawClasspath(rawClasspath, null);
        } catch (JavaModelException e) {
            throw new RuntimeException("Trouble deleting!", e);
        }
    }

    @Override
    public void undo() {
        // re-add the target dependency from the project
        try {
            IClasspathEntry[] rawClasspath = getDependency().getSource().getRawClasspath();
            IClasspathEntry[] newClasspath = new IClasspathEntry[rawClasspath.length + 1];
            System.arraycopy(rawClasspath, 0, newClasspath, 0, getDeletedPosition());
            newClasspath[getDeletedPosition()] = getDeletedEntry();
            System.arraycopy(rawClasspath, getDeletedPosition(), newClasspath, getDeletedPosition() + 1, rawClasspath.length - getDeletedPosition());
            getDependency().getSource().setRawClasspath(newClasspath, null);
        } catch (JavaModelException e) {
            throw new RuntimeException("Trouble deleting!", e);
        }
    }
}
