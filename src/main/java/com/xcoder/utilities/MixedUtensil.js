/**
 * 数组类型检查
 * @param array
 */
function arrayTypeCheck(array) {
    if (typeof array != 'Array') {
        throw 'type error object is not Array';
    }
}

/**
 * 数组未定义或null
 * @param array
 * @returns {boolean}
 */
function arrayIsNone(array) {
    return !array;
}

/**
 * 数组空判断
 * @param array
 * @returns {boolean}
 */
function arrayIsEmpty(array) {
    arrayTypeCheck(array);
    return arrayIsNone(array) || 0 >= array.length;
}

/**
 * 数组空判断
 * @param array
 * @returns {boolean}
 */
function arrayIsNotEmpty(array) {
    return !arrayIsEmpty(array);
}

/**
 * 数组未定义或null检查
 * @param array
 */
function arrayNoneCheck(array) {
    if (arrayIsNone(array)) {
        throw 'array undefined or null error';
    }
}

/**
 * 数组空检查
 * @param array
 */
function arrayEmptyCheck(array) {
    if (arrayIsEmpty(array)) {
        throw 'array empty error';
    }
}

/**
 * 数组元素交换位置
 * @param array
 * @param idx0
 * @param idx1
 * @returns {*}
 */
function arraySwap(array, idx0, idx1) {
    arrayNoneCheck(array);
    array[idx0] = array.splice(idx1, 1, array[idx0])[0];
    return array;
}

/**
 * 数组元素前移动一位
 * @param array
 * @param idx
 */
function arrayMoveUp(array, idx) {
    if (0 != idx) {
        arraySwap(array, idx, idx - 1);
    }
}

/**
 * 数组元素后移动一位
 * @param array
 * @param idx
 */
function arrayMoveDown(array, idx) {
    if (idx != array.length - 1) {
        arraySwap(array, idx, idx + 1);
    }
}

/**
 * 数组空判断
 * @returns {boolean}
 */
Array.prototype.isEmpty = function () {
    return arrayIsEmpty(this);
};

/**
 * 数组空判断
 * @returns {boolean}
 */
Array.prototype.isNotEmpty = function () {
    return arrayIsNotEmpty(this);
};

/**
 * 数组元素交换位置
 * @param idx0
 * @param idx1
 */
Array.prototype.swap = function (idx0, idx1) {
    arraySwap(this, idx0, idx1);
};

/**
 * 数组元素前移一位
 * @param idx
 */
Array.prototype.moveUp = function (idx) {
    arrayMoveUp(this, idx);
};

/**
 * 数组元素后移一位
 * @param idx
 */
Array.prototype.moveDown = function (idx) {
    arrayMoveDown(this, idx);
};


/**
 * SplicingTransfer字符串拼接利器
 * @returns {string}
 * @constructor
 */
String.prototype.ST = function () {
    return '\'' + this + '\'';
};

/**
 * input hidden 虚拟dom
 * @param name
 * @param value
 * @returns {HTMLElement}
 */
function getVirtualHidden(name, value) {
    var hidden = document.createElement("input");
    hidden.type = 'hidden';
    hidden.value = value;
    hidden.name = name;
    return hidden;
}