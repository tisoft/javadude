/*******************************************************************************
 * Copyright (c) 2008 Scott Stanchfield
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.javadude.workingsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;

public class NatureWorkingSetUpdater implements IWorkingSetUpdater {
	private static final Map<String, IWorkingSet> workingSets_ = Collections.synchronizedMap(new HashMap<String, IWorkingSet>());
	static {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					if (event.getDelta() == null) {
						return;
					}
					event.getDelta().accept(new IResourceDeltaVisitor() {
						@Override
						public boolean visit(IResourceDelta delta) throws CoreException {
							IProject project = null;
							switch (delta.getKind()) {
								case IResourceDelta.ADDED:
									// if it's a project being added
									if (delta.getResource() instanceof IProject) {
										// add to appropriate working set based on project natures
										project = (IProject) delta.getResource();
										addToWorkingSets(project, null);
										return false;
									}
									break;
								case IResourceDelta.CHANGED: // natures change or project opened/closed
									if ((delta.getResource() instanceof IProject) && (delta.getFlags() & IResourceDelta.OPEN) != 0) {
										project = (IProject) delta.getResource();

									} else if (".project".equals(delta.getResource().getName())) {
										// natures could have changed -- might need to update the working sets
										project = delta.getResource().getProject();
									}
									if (project != null) {
										Set<IWorkingSet> setsContainingProject = setsContainingProject(project);
										// add to appropriate working sets
										addToWorkingSets(project, setsContainingProject);
										for (IWorkingSet workingSet : setsContainingProject) {
	                                        removeFromWorkingSet(workingSet, project);
                                        }
										return false;
									}
									break;
								case IResourceDelta.REMOVED:
									if (delta.getResource() instanceof IProject) {
										project = (IProject) delta.getResource();
										// remove from all working sets
										Set<IWorkingSet> setsContainingProject = setsContainingProject(project);
										for (IWorkingSet workingSet : setsContainingProject) {
											removeFromWorkingSet(workingSet, project);
                                        }
										return false;
									}
									break;
							}
							return true;
						}
					});
				} catch (CoreException e) {
					Activator.getUtil().error(2, "Error walking delta", e);
				}
			}

			private void removeFromWorkingSet(IWorkingSet workingSet, IProject project) {
				IAdaptable[] elements = workingSet.getElements();
				if (elements.length == 0) {
					return;
				}
				List<IAdaptable> newElements = new ArrayList<IAdaptable>();
				boolean found = false;
				for (IAdaptable adaptable : elements) {
					if (adaptable != project) {
						newElements.add(adaptable);
					} else {
						found = true;
					}
				}
				if (!found) {
					return;
				}
				workingSet.setElements(newElements.toArray(new IAdaptable[newElements.size()]));
			}
			private Set<IWorkingSet> setsContainingProject(IProject project) {
				Set<IWorkingSet> workingSetsContainingProject = new HashSet<IWorkingSet>();
				for (IWorkingSet workingSet : NatureWorkingSetUpdater.workingSets_.values()) {
					IAdaptable[] elements = workingSet.getElements();
					for (IAdaptable element : elements) {
	                    if (element.equals(project)) {
	                    	workingSetsContainingProject.add(workingSet);
	                    }
                    }
				}
				return workingSetsContainingProject;
			}
			private void addToWorkingSets(IProject project, Set<IWorkingSet> setsContainingProject) {
				if (!project.isOpen()) {
					return;
				}
				for (Map.Entry<String, IWorkingSet> entry : NatureWorkingSetUpdater.workingSets_.entrySet()) {
					try {
						if (NatureWorkingSetUpdater.projectHasNature(project, entry.getKey())) {
							// add project to working set
							IWorkingSet workingSet = entry.getValue();
							if (setsContainingProject != null) {
								setsContainingProject.remove(workingSet);
							}
							IAdaptable[] elements = workingSet.getElements();
							IAdaptable[] newElements = new IAdaptable[elements.length + 1];
							System.arraycopy(elements, 0, newElements, 0, elements.length);
							newElements[elements.length] = project;
							workingSet.setElements(newElements);
							break;
						}
					} catch (CoreException e) {
						Activator.getUtil().error(42, "Core exception while checking project nature", e);
					}
				}
			}});
	}

	public static boolean projectHasNature(IProject project, String natureList) throws CoreException {
		StringTokenizer stringTokenizer = new StringTokenizer(natureList, ", ");
		while (stringTokenizer.hasMoreTokens()) {
			String nature = stringTokenizer.nextToken();
			if (project.hasNature(nature)) {
				return true;
			}
		}
		return false;
	}
	public NatureWorkingSetUpdater() {
		// assign working sets based on reading projects in workspace

	}

	private String natureId(IWorkingSet workingSet) {
		String id = workingSet.getName();
		return id.substring("Nature: ".length());
	}

	@Override
	public void add(IWorkingSet workingSet) {
		NatureWorkingSetUpdater.workingSets_.put(natureId(workingSet), workingSet);
	}

	@Override
	public boolean contains(IWorkingSet workingSet) {
		return NatureWorkingSetUpdater.workingSets_.values().contains(workingSet);
	}

	@Override
	public void dispose() {
		NatureWorkingSetUpdater.workingSets_.clear();
	}

	@Override
	public boolean remove(IWorkingSet workingSet) {
		return NatureWorkingSetUpdater.workingSets_.remove(natureId(workingSet)) != null;
	}
}
