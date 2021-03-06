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
package org.loboevolution.html.domimpl;


import org.loboevolution.html.dombl.ModelNode;
import org.loboevolution.html.renderstate.BaseFontRenderState;
import org.loboevolution.html.renderstate.RenderState;
import org.loboevolution.html.style.AbstractCSSProperties;
import org.loboevolution.html.style.ComputedCSSProperties;
import org.loboevolution.html.style.FontValues;
import org.loboevolution.html.style.HtmlValues;
import org.loboevolution.w3c.html.HTMLBaseFontElement;

/**
 * The Class HTMLBaseFontElementImpl.
 */
public class HTMLBaseFontElementImpl extends HTMLAbstractUIElement implements HTMLBaseFontElement {

	/**
	 * Instantiates a new HTML base font element impl.
	 *
	 * @param name
	 *            the name
	 */
	public HTMLBaseFontElementImpl(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLBaseFontElement#getColor()
	 */
	@Override
	public String getColor() {
		return this.getAttribute(COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLBaseFontElement#getFace()
	 */
	@Override
	public String getFace() {
		return this.getAttribute(FACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.w3c.html.HTMLBaseFontElement#setColor(java.lang.String)
	 */
	@Override
	public void setColor(String color) {
		this.setAttribute(COLOR, color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.w3c.html.HTMLBaseFontElement#setFace(java.lang.String)
	 */
	@Override
	public void setFace(String face) {
		this.setAttribute(FACE, face);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLBaseFontElement#getSize()
	 */
	@Override
	public int getSize() {
		String valueText = this.getAttribute(SIZE);
		return HtmlValues.getPixelSize(valueText, this.getRenderState(), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLBaseFontElement#setSize(int)
	 */
	@Override
	public void setSize(int size) {
		this.setAttribute(SIZE, String.valueOf(size));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.html.domimpl.HTMLElementImpl#createRenderState(org.
	 * loboevolution .html.renderstate.RenderState)
	 */
	@Override
	protected RenderState createRenderState(RenderState prevRenderState) {
		String size = this.getAttribute(SIZE);
		if (size != null) {
			int fontNumber = FontValues.getFontNumberOldStyle(size, prevRenderState);
			prevRenderState = new BaseFontRenderState(prevRenderState, fontNumber);
		}
		return super.createRenderState(prevRenderState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.html.domimpl.HTMLElementImpl#createDefaultStyleSheet()
	 */
	@Override
	protected AbstractCSSProperties createDefaultStyleSheet() {
		String fontFamily = this.getAttribute(FACE);
		String color = this.getAttribute(COLOR);
		String size = this.getAttribute(SIZE);
		ModelNode parentModelNode = this.getParentModelNode();
		RenderState parentRS = parentModelNode == null ? null : parentModelNode.getRenderState();
		String fontSize = null;
		if (parentRS != null) {
			int fontNumber = FontValues.getFontNumberOldStyle(size, parentRS);
			fontSize = FontValues.getFontSizeSpec(fontNumber);
		}
		ComputedCSSProperties css = new ComputedCSSProperties(this);
		if (fontSize != null) {
			css.internalSetLC("font-size", fontSize);
		}
		if (fontFamily != null) {
			css.internalSetLC("font-family", fontFamily);
		}
		if (color != null) {
			css.internalSetLC(COLOR, color);
		}
		return css;
	}

}
