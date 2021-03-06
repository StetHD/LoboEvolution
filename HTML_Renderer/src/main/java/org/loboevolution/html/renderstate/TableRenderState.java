/*
    GNU GENERAL LICENSE
    Copyright (C) 2014 - 2018 Lobo Evolution

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General License for more details.

    You should have received a copy of the GNU General Public
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

    Contact info: ivan.difrancesco@yahoo.it
 */
package org.loboevolution.html.renderstate;

import java.awt.Color;

import org.loboevolution.html.domimpl.HTMLElementImpl;
import org.loboevolution.html.domimpl.HTMLTableElementImpl;
import org.loboevolution.html.info.BackgroundInfo;
import org.loboevolution.util.gui.ColorFactory;

/**
 * The Class TableRenderState.
 */
public class TableRenderState extends StyleSheetRenderState {
	
	/** The background info. */
	private BackgroundInfo backgroundInfo = INVALID_BACKGROUND_INFO;

	/**
	 * Instantiates a new table render state.
	 *
	 * @param prevRenderState
	 *            the prev render state
	 * @param element
	 *            the element
	 */
	public TableRenderState(RenderState prevRenderState, HTMLElementImpl element) {
		super(prevRenderState, element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.html.renderstate.StyleSheetRenderState#
	 * getTextBackgroundColor ()
	 */
	@Override
	public Color getTextBackgroundColor() {
		return super.getTextBackgroundColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.html.renderstate.StyleSheetRenderState#getDefaultDisplay(
	 * )
	 */
	@Override
	protected int getDefaultDisplay() {
		return DISPLAY_TABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.html.renderstate.StyleSheetRenderState#invalidate()
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		this.backgroundInfo = INVALID_BACKGROUND_INFO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.html.renderstate.StyleSheetRenderState#getBackgroundInfo(
	 * )
	 */
	@Override
	public BackgroundInfo getBackgroundInfo() {
		BackgroundInfo binfo = this.backgroundInfo;
		if (binfo != INVALID_BACKGROUND_INFO) {
			return binfo;
		}
		// Apply style based on deprecated attributes.
		binfo = super.getBackgroundInfo();
		HTMLTableElementImpl element = (HTMLTableElementImpl) this.element;
		if (binfo == null || binfo.getBackgroundColor() == null) {
			String bgColor = element.getBgColor();
			if (bgColor != null && !"".equals(bgColor)) {
				Color bgc = ColorFactory.getInstance().getColor(bgColor);
				if (binfo == null) {
					binfo = new BackgroundInfo();
				}
				binfo.setBackgroundColor(bgc);
			}
		}
		this.backgroundInfo = binfo;
		return binfo;
	}
}
