/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.webtrans.client.editor.table;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the table.
 */
public interface TableResources extends ClientBundle
{

   /**
    * An {@link ClientBundle} that provides images for
    * {@link InlineTargetCellEditor}.
    */
   @Source("org/zanata/webtrans/images/crystal_project/_16x16/actions/3floppy_unmount.png")
   ImageResource cellEditorAccept();

   @Source("org/zanata/webtrans/images/crystal_project/_16x16/actions/button_cancel.png")
   ImageResource cellEditorCancel();

   @Source("org/zanata/webtrans/images/crystal_project/_16x16/actions/flag.png")
   ImageResource cellEditorFuzzy();

   @Source("org/zanata/webtrans/images/crystal_project/_16x16/actions/2rightarrow.png")
   ImageResource copySrcButton();

   @Source("org/zanata/webtrans/images/validate.png")
   ImageResource cellEditorValidate();
}
