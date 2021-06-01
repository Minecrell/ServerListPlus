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
		return format('0');
	}

	public ChatBuilder darkBlue() {
		return format('1');
	}

	public ChatBuilder darkGreen() {
		return format('2');
	}

	public ChatBuilder darkAqua() {
		return format('3');
	}

	public ChatBuilder darkRed() {
		return format('4');
	}

	public ChatBuilder darkPurple() {
		return format('5');
	}

	public ChatBuilder gold() {
		return format('6');
	}

	public ChatBuilder gray() {
		return format('7');
	}

	public ChatBuilder darkGray() {
		return format('8');
	}

	public ChatBuilder blue() {
		return format('9');
	}

	public ChatBuilder green() {
		return format('a');
	}

	public ChatBuilder aqua() {
		return format('b');
	}

	public ChatBuilder red() {
		return format('c');
	}

	public ChatBuilder lightPurple() {
		return format('d');
	}

	public ChatBuilder yellow() {
		return format('e');
	}

	public ChatBuilder white() {
		return format('f');
	}

	public ChatBuilder obfuscated() {
		return format('k');
	}

	public ChatBuilder bold() {
		return format('l');
	}

	public ChatBuilder strikeThrough() {
		return format('m');
	}

	public ChatBuilder underline() {
		return format('n');
	}

	public ChatBuilder italic() {
		return format('o');
	}

	public ChatBuilder reset() {
		return format('r');
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
