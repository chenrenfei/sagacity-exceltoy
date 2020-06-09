package org.sagacity.tools.exceltoy.convert.impl;

import org.sagacity.tools.exceltoy.convert.AbstractConvert;

/**
 * 
 * @author zhong
 *
 */
public class LengthConvert extends AbstractConvert {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8188882142110645117L;

	@Override
	public Object convert(Object param) throws Exception {
		if (param == null) {
			return 0;
		}
		param = super.replaceParams(param);
		return param.toString().length();
	}

	@Override
	public void reset() {

	}

}
