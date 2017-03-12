package com.airbnb.epoxy;

final class QualifiedId {

  final String packageName;
  final int id;

  public QualifiedId(String packageName, int id) {
    this.packageName = packageName;
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    QualifiedId that = (QualifiedId) o;

    if (id != that.id) {
      return false;
    }
    return packageName != null ? packageName.equals(that.packageName) : that.packageName == null;
  }

  @Override
  public int hashCode() {
    int result = packageName != null ? packageName.hashCode() : 0;
    result = 31 * result + id;
    return result;
  }

  @Override
  public String toString() {
    return "QualifiedId{" +
        "packageName='" + packageName + '\'' +
        ", id=" + id +
        '}';
  }
}
