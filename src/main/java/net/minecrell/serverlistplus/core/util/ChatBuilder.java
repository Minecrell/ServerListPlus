/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.core.util;

public class ChatBuilder implements CharSequence {
	private final StringBuilder sb = new StringBuilder();

	public ChatBuilder append(CharSequence s) {
		sb.append(s);
		return this;
	}

	public ChatBuilder append(Object o) {
		sb.append(o.toString());
		return this;
	}

	public ChatBuilder append(char c) {
		sb.append(c);
		return this;
	}

	public ChatBuilder black() {
		format('0');
		return this;
	}

	public ChatBuilder darkBlue() {
		format('1');
		return this;
	}

	public ChatBuilder darkGreen() {
		format('2');
		return this;
	}

	public ChatBuilder darkAqua() {
		format('3');
		return this;
	}

	public ChatBuilder darkRed() {
		format('4');
		return this;
	}

	public ChatBuilder darkPurple() {
		format('5');
		return this;
	}

	public ChatBuilder gold() {
		format('6');
		return this;
	}

	public ChatBuilder gray() {
		format('7');
		return this;
	}

	public ChatBuilder darkGray() {
		format('8');
		return this;
	}

	public ChatBuilder blue() {
		format('9');
		return this;
	}

	public ChatBuilder green() {
		format('a');
		return this;
	}

	public ChatBuilder aqua() {
		format('b');
		return this;
	}

	public ChatBuilder red() {
		format('c');
		return this;
	}

	public ChatBuilder lightPurple() {
		format('d');
		return this;
	}

	public ChatBuilder yellow() {
		format('e');
		return this;
	}

	public ChatBuilder white() {
		format('f');
		return this;
	}

	public ChatBuilder obfuscated() {
		format('k');
		return this;
	}

	public ChatBuilder bold() {
		format('l');
		return this;
	}

	public ChatBuilder strikeThrough() {
		format('m');
		return this;
	}

	public ChatBuilder underline() {
		format('n');
		return this;
	}

	public ChatBuilder italic() {
		format('o');
		return this;
	}

	public ChatBuilder reset() {
		format('r');
		return this;
	}

	public ChatBuilder format(char c) {
		return append('§').append(c);
	}

	@Override
	public int length() {
		return sb.length();
	}

	@Override
	public char charAt(int index) {
		return sb.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return sb.subSequence(start, end);
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ChatBuilder that = (ChatBuilder) o;
		return sb.equals(that.sb);
	}

	@Override
	public int hashCode() {
		return sb.hashCode();
	}
}
